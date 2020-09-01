package com.merax.livecalc

import com.merax.livecalc.v1.Storage.*
import order.livecalc.v1.Components.Discount
import order.livecalc.v1.Components.Order
import order.livecalc.v1.Storage.*
import order.livecalc.v1.Storage.CashIn
import order.livecalc.v1.Storage.OrderSettings
import org.junit.Assert.*
import org.junit.jupiter.api.Test

data class ProductMap(
    val id: Int,
    val quantity: Int,
    val price: Float
)

class DiscountSameProductUnitTest {
    private var storage: Storage = Storage()
    private var orderComponent = Order(storage)
    private var discountComponent = Discount(storage)

    private var discounts: HashMap<String, DiscountIN> = hashMapOf(
        "10+1_same" to DiscountIN(
            id = 1,
            condition = DiscountCondition(
                type = DiscountConditionTypes.QUANTITY,
                apply = DiscountConditionApply.FOREACH,
                content = listOf(
                    DiscountConditionContent(
                        id = listOf(1),
                        value = 10.0F
                    )
                )
            ),
            result = DiscountResult(
                type = DiscountResultTypes.PRODUCTS,
                match = DiscountResultMatch.AND,
                content = listOf(
                    DiscountResultContent(
                        id = 1,
                        value = 1.0F
                    )
                )
            )
        ),
        "10+1_other" to DiscountIN(
            id = 2,
            condition = DiscountCondition(
                type = DiscountConditionTypes.QUANTITY,
                apply = DiscountConditionApply.FOREACH,
                content = listOf(
                    DiscountConditionContent(
                        id = listOf(1),
                        value = 10.0F
                    )
                )
            ),
            result = DiscountResult(
                type = DiscountResultTypes.PRODUCTS,
                match = DiscountResultMatch.AND,
                content = listOf(
                    DiscountResultContent(
                        id = 2,
                        value = 1.0F
                    )
                )
            )
        ),
        "10+select_1_from_2" to DiscountIN(
            id = 3,
            condition = DiscountCondition(
                type = DiscountConditionTypes.QUANTITY,
                apply = DiscountConditionApply.FOREACH,
                content = listOf(
                    DiscountConditionContent(
                        id = listOf(1),
                        value = 10.0F
                    )
                )
            ),
            result = DiscountResult(
                type = DiscountResultTypes.PRODUCTS,
                match = DiscountResultMatch.OR,
                content = listOf(
                    DiscountResultContent(
                        id = 2,
                        value = 1.0F
                    ),
                    DiscountResultContent(
                        id = 2,
                        value = 1.0F
                    )
                )
            )
        )
    )

    init {
        initDataProvider()
    }

    private fun initDataProvider() {
        storage.data = StorageData(
            input = InputData(
                products = hashMapOf(),
                discounts = hashMapOf(),
                orderSettings = OrderSettings(
                    splitRatioJuridical = 0.0F,
                    requiredCashIn = 0.0F
                ),
                cashIn = CashIn(
                    juridical = CashInItem(),
                    physical = CashInItem()
                )
            ),
            output = OutputData(
                products = hashMapOf(),
                discounts = hashMapOf(),
                orders = Orders(
                    basic = null,
                    additional = null,
                    zeroPrice = null
                ),
                writeOff = WriteOff(
                    official = listOf(),
                    unofficial = listOf()
                ),
                cash = Cash(
                    juridical = 0.0F,
                    physical = 0.0F
                ),
                bonusPoints = 0,
                requiredCashIn = Cash(
                    juridical = 0.0F,
                    physical = 0.0F
                ),
                orderSettings = OrderParams(
                    clientType = ClientType.JURIDICAL
                )
            )
        )
    }

    private fun generateProductVariants(products: List<ProductMap>): HashMap<Int, Product>{
        val resultContent = hashMapOf<Int, Product>()
        for(product in products){
            resultContent[product.id] = Product(
                id = product.id,
                quantity = product.quantity,
                price = product.price
            )
        }
        return resultContent
    }

    @Test
    fun emptyDiscounts() {
        storage.data.input.products = generateProductVariants(listOf(
            ProductMap(1, 10, 3.23F)
        ))

        val emptyDiscounts: HashMap<Int, DiscountOutput> = hashMapOf()
        orderComponent.calculateProductAmount(storage.data.input.products)
        discountComponent.createMap(storage.data.input)
        discountComponent.apply(storage.data.input.discounts)
        assertEquals(emptyDiscounts, storage.data.output.discounts)
    }

    @Test
    fun testN_FOR_M_same() {
        /*********Test 10 + 1: not enough product quantity selected*/
        storage.data.input.discounts[1] = discounts["10+1_same"]!!
        storage.data.input.products = generateProductVariants(listOf(
            ProductMap(1, 8, 3.23F),
            ProductMap(2, 10, 3.45F),
            ProductMap(3, 10, 3.45F)
        ))
        storage.data.output.discounts = hashMapOf()

        orderComponent.calculateProductAmount(storage.data.input.products)
        discountComponent.createMap(storage.data.input)
        discountComponent.apply(storage.data.input.discounts)
        assertFalse("10+1: not enough product quantity selected", storage.data.output.discounts.isNotEmpty())

        /********Test 10 + 1: enough product quantity selected*/
        storage.data.input.products = generateProductVariants(listOf(
            ProductMap(1, 11, 3.23F),
            ProductMap(2, 10, 3.45F),
            ProductMap(3, 10, 3.45F)
        ))
        storage.data.output.discounts = hashMapOf()

        orderComponent.calculateProductAmount(storage.data.input.products)
        discountComponent.createMap(storage.data.input)
        discountComponent.apply(storage.data.input.discounts)
        assertEquals("10+1: enough product quantity selected", 1, storage.data.output.discounts[1]!!.products[1]!!)

        /*******Test 7 + 3: enough product quantity selected*/
        storage.data.input.discounts[1]?.condition?.content = listOf(DiscountConditionContent(listOf(1), 7.0F))
        storage.data.input.discounts[1]?.result?.content = listOf(DiscountResultContent(1, 3.0F))
        storage.data.input.products = generateProductVariants(listOf(
            ProductMap(1, 15, 3.23F),
            ProductMap(2, 10, 3.45F),
            ProductMap(3, 10, 3.45F)
        ))
        storage.data.output.discounts = hashMapOf()
        orderComponent.calculateProductAmount(storage.data.input.products)
        discountComponent.createMap(storage.data.input)
        discountComponent.apply(storage.data.input.discounts)
        println(storage.data.output.discounts)
        assertNotNull("7+3: enough product quantity selected", storage.data.output.discounts)
        assertNotNull("7+3: enough product quantity selected", storage.data.output.discounts[1])
        assertNotNull("7+3: enough product quantity selected", storage.data.output.discounts[1]!!.products[1])
        assertEquals("7+3: enough product quantity selected", 6, storage.data.output.discounts[1]!!.products[1]!!)
    }

    @Test
    fun testAvailableProducts(){
        /***** 10 + 3 (available for bonus 4)*/
        storage.data.input.products = generateProductVariants(listOf(
            ProductMap(1, 11, 3.23F),
            ProductMap(2, 10, 3.45F),
            ProductMap(3, 10, 3.45F)
        ))
        storage.data.output.discounts = hashMapOf()
        storage.data.input.discounts[1] = discounts["10+1_same"]!!
        storage.data.input.discounts[1]?.condition?.content = listOf(DiscountConditionContent(listOf(1), 10.0F))
        storage.data.input.discounts[1]?.condition?.apply = DiscountConditionApply.ONCE
        storage.data.input.discounts[1]?.result?.content = listOf(DiscountResultContent(1, 3.0F))
        storage.data.input.products[1]?.available = 15

        orderComponent.calculateProductAmount(storage.data.input.products)
        discountComponent.createMap(storage.data.input)
        discountComponent.apply(storage.data.input.discounts)
        println(storage.data.output.discounts)
        assertNotNull("7+3: enough product quantity selected", storage.data.output.discounts)
        assertNotNull("7+3: enough product quantity selected", storage.data.output.discounts[1])
        assertNotNull("7+3: enough product quantity selected", storage.data.output.discounts[1]!!.products[1])
        assertEquals("10+1: enough product quantity selected", 3, storage.data.output.discounts[1]!!.products[1]!!)
    }
}