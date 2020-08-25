package order.livecalc.v1.Components

import order.livecalc.v1.Storage.*
import order.livecalc.v1.Storage.Order as OrderItem
import order.livecalc.v1.Components.Utils as Utils

/** Order calculation component
 * @property orders - list of orders (basic, additional, zero_price)
 * */
class Order(val storage: Storage) : IComponent {

    fun calculateProductAmount(
        products: HashMap<Int, Product> = hashMapOf(),
        productsSelected: HashMap<Int, Product>
    ): HashMap<Int, Product> {

        if (products.isNullOrEmpty()) {
            return hashMapOf()
        }
        if (productsSelected.isEmpty()) {
            return hashMapOf()
        }
        val productsResult = hashMapOf<Int, Product>()
        var orderAmount = 0.0F

        for ((productID, product) in productsSelected) {
            if (product.quantity > 0) {
                product.id = productID
                product.price = Utils().roundUp(products[productID]!!.price, 2)
                product.priceDiscounted = product.price
                product.amount = Utils().roundUp(products[productID]!!.price * product.quantity, 2)
                product.amountDiscounted = product.amount
                productsResult[productID] = product
                orderAmount += product.amount
            }
        }
        storage.data.output.orders.basic = OrderItem(
            clientType = ClientType.PHYSICAL,
            products = productsResult,
            amount = orderAmount,
            amountDiscounted = orderAmount
        )

        return productsResult
    }

    fun calculateRequiredCashIn() {
        storage.data.output.requiredCashIn.juridical =
            Utils().roundUp(
                storage.data.output.orders.additional!!.amountDiscounted / 100 * storage.data.input.orderSettings.requiredCashIn,
                2
            )
        storage.data.output.requiredCashIn.physical =
            Utils().roundUp(
                storage.data.output.orders.basic!!.amountDiscounted / 100 * storage.data.input.orderSettings.requiredCashIn,
                2
            )
    }

    fun split() {
        val orders: Orders = Orders(
            basic = OrderItem(
                clientType = ClientType.PHYSICAL,
                products = hashMapOf()
            ),
            additional = OrderItem(
                clientType = ClientType.JURIDICAL,
                products = hashMapOf()
            ),
            zeroPrice = OrderItem(
                clientType = ClientType.JURIDICAL,
                products = hashMapOf()
            )
        )

        if(storage.data.input.orderSettings.selectedClientType === ClientType.JURIDICAL){
            storage.data.input.orderSettings.splitRatioJuridical = 0.0F
        }

        for ((productID, product) in storage.data.output.products) {
            val basicQuantity =
                Utils().roundDown(
                    product.quantity * (100 - storage.data.input.orderSettings.splitRatioJuridical) / 100,
                    0
                ).toInt()
            val additionalQuantity = product.quantity - basicQuantity

            if (storage.data.output.zeroOrderProducts.containsKey(productID)) {
                orders.zeroPrice!!.products[productID] =
                    Product(
                        id = productID,
                        quantity = storage.data.output.zeroOrderProducts[productID]!!,
                        price = 0.0F,
                        amount = 0.0F,
                        priceDiscounted = 0.0F,
                        amountDiscounted = 0.0F
                    )
            }

            orders.basic!!.products[productID] = Product(
                id = productID,
                quantity = basicQuantity,
                price = product.price,
                amount = product.price * basicQuantity,
                priceDiscounted = product.price,
                amountDiscounted = product.price * basicQuantity
            )

            orders.additional!!.products[productID] = Product(
                id = productID,
                quantity = additionalQuantity,
                price = product.price,
                amount = product.price * additionalQuantity,
                priceDiscounted = product.price,
                amountDiscounted = product.price * additionalQuantity
            )

            orders.basic!!.amount += product.price * basicQuantity
            orders.basic!!.amountDiscounted += product.price * basicQuantity
            orders.additional!!.amount += product.price * additionalQuantity
            orders.additional!!.amountDiscounted += product.price * additionalQuantity
        }
        calculateTotals(orders)

        storage.data.output.orders = orders
    }

    private fun calculateTotals(orders: Orders) {
        if(orders.basic!!.amount > 0){
            orders.basic!!.amount = Utils().roundUp(orders.basic!!.amount, 2)
            orders.basic!!.amountDiscounted = Utils().roundUp(orders.basic!!.amountDiscounted, 2)
        } else {
            orders.basic = null
        }

        if(orders.additional!!.amount > 0){
            orders.additional!!.amount = Utils().roundUp(orders.additional!!.amount, 2)
            orders.additional!!.amountDiscounted = Utils().roundUp(orders.additional!!.amountDiscounted, 2)
        } else {
            orders.additional = null
        }

        if(!orders.zeroPrice!!.products.isNullOrEmpty()){
            orders.zeroPrice!!.amount = Utils().roundUp(orders.zeroPrice!!.amount, 2)
            orders.zeroPrice!!.amountDiscounted = Utils().roundUp(orders.zeroPrice!!.amountDiscounted, 2)
        } else {
            orders.zeroPrice = null
        }
    }
}