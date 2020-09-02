package order.livecalc.v1.Components

import com.merax.livecalc.v1.Storage.DiscountConditionApply
import com.merax.livecalc.v1.Storage.DiscountIN
import com.merax.livecalc.v1.Storage.DiscountResultTypes
import order.livecalc.v1.Components.DiscountConditions.CheckCondition
import order.livecalc.v1.Storage.*
import kotlin.math.floor
import kotlin.math.min

/** Discount calculation component
 * @property discountProductsMap - discount to product map
 * @property productTotals - product discount applied totals
 * @property discountTotals - discount applied totals
 * @property orderTotals - order discount applied totals
 * @property bonusPoints - bonus points amount
 * @property appliedDiscounts - list of applied discounts
 * */
class Discount(val storage: Storage) : IComponent {
    private var discountProductsMap: HashMap<Int, HashMap<Int, Product>> = hashMapOf()
    private var productTotals: HashMap<Int, Product> = hashMapOf()
    private var discountTotals: HashMap<Int, Product> = hashMapOf()
    private var orderTotals: Product =
        Product(0, false, 0, 0.0F)
    private var bonusPoints: Int = 0
    private var appliedDiscounts: HashMap<Int, DiscountOutput> = hashMapOf()
    private var zeroOrderProducts: HashMap<Int, Int> = hashMapOf()
    private var checkCondition: CheckCondition = CheckCondition()

    fun getBonusPoints() = bonusPoints
    fun getAppliedDiscounts() = appliedDiscounts
    fun getZeroOrderProducts() = zeroOrderProducts

    fun init(discounts: HashMap<Int, DiscountIN>) {
        for ((_, discount) in discounts) {
            discount.canBeApplied = true
        }
    }

    fun filterOnViewEnter() {
        for ((_, discount) in storage.data.input.discounts) {
            discount.canBeApplied = discount.canBeApplied
        }
    }

    /*private fun filterByOrderHistory(discount: DiscountInput): Boolean {
        val history = storage.data.input.salesHistory
        return history.client.month[1]!! > discount.applyConditions.salesHistory.salesAmountFrom
    }*/
    private fun filterByClientSettings(discount: DiscountIN): Boolean {
        return true
    }


    /**
     * Apply list of discounts
     * @param discounts [HashMap] - list of discounts to be applied
     * @param products [HashMap] - list of products in order
     */
    fun apply(discounts: HashMap<Int, DiscountIN>) {
        this.appliedDiscounts = hashMapOf()
        for ((_, discount) in discounts) {
            if (!this.canApply(discount))
                continue
            this.apply(discount)
        }
        storage.data.output.discounts = appliedDiscounts
    }

    /**
     * Check if one discount can be applied
     * @param discount[DiscountInput] - discount item
     * @return canBeApplied [Boolean]
     */

    private fun canApply(discount: DiscountIN) =
        canBeCombined(discount) && checkApplyCondition(discount)

    /**
     * Check if discount condition satisfy requirements
     * @param discount[DiscountInput] - discount item
     * @return checkApplyCondition [Boolean]
     * */
    private fun checkApplyCondition(discount: DiscountIN): Boolean =
        checkCondition.checkAll(discount)

    /**
     * Check if discount can be combined with previously applied discounts
     * @param discount[DiscountInput] - discount item
     * @return canBeCombined [Boolean]
     * */
    private fun canBeCombined(discount: DiscountIN) = true
    /*discount.combineWith!!.isEmpty() || appliedDiscounts.isEmpty() || (discount.combineWith?.intersect(
        appliedDiscounts.keys
    ))!!.isNotEmpty()*/

    /**
     * Apply one of discounts
     * @param discount [DiscountInput] - discount item
     *
     */
    private fun apply(discount: DiscountIN) {
        var discountSum = 0.0F
        var discountBonusPoints = 0
        var discountProducts: HashMap<Int, Int> = hashMapOf()
        val discountProductsToSelect: HashMap<Int, Int> = hashMapOf()
        when (discount.result.type) {
            DiscountResultTypes.PRODUCTS -> discountProducts = generateBonusProducts(discount)
            else -> return
        }

        if (discountProducts.size > 0) {
            this.appliedDiscounts[discount.id] =
                DiscountOutput(
                    discount.id,
                    discountSum,
                    discountProducts,
                    discountProductsToSelect,
                    discountBonusPoints
                )
        }
    }

    /**
     * Calculate bonus sum
     * @param discount [DiscountInput] - list of discounts to be applied
     * @param products [HashMap] - list of products in order
     * @return discountSum [Float] - discount sum
     */
    private fun calculateBonusSum(
        discount: DiscountIN,
        products: HashMap<Int, Product>
    ): Float {
        var discountSum = 0.0F
        /*products.forEach {
            val calculatedValue = Utils().roundDown(
                calculatedValue(
                    discount,
                    when (discount.priceToApply) {
                        DiscountPriceToApplyType.DISCOUNTED_PRICE -> it.value.priceDiscounted
                        DiscountPriceToApplyType.BASIC_PRICE -> it.value.price
                    },
                    products
                ), 2
            )
            it.value.appliedDiscounts!!.add(calculatedValue)
            it.value.priceDiscounted -= calculatedValue
            it.value.amountDiscounted = it.value.quantity * it.value.priceDiscounted
            discountSum += calculatedValue
        }*/
        return discountSum
    }

    /**
     * Generate bonus products
     * @param discount [DiscountInput] - list of discounts to be applied
     * @return bonusProducts [HashMap] - list of bonus products
     */
    private fun generateBonusProducts(
        discount: DiscountIN
    ): HashMap<Int, Int> {
        val bonusProducts: HashMap<Int, Int> = hashMapOf()

        discount.result.content.forEach { product ->
            val quantityRequired = discount.condition.content.first { it.id.contains(product.id) }.value
            val quantityGenerated = product.value
            val quantitySelected = productTotals[product.id]?.quantity!!
            val availableToSelect =
                productTotals[product.id]?.available!! - quantitySelected - storage.data.input.products[product.id]!!.bonusTotalSelectedQuantity

            if (availableToSelect < 1 || quantityRequired < 1 || quantityGenerated < 1 || quantitySelected < 1 || quantitySelected < quantityRequired) {
                return@forEach
            }

            val coefficient = when (discount.condition.apply) {
                DiscountConditionApply.FOREACH -> {
                    floor(quantitySelected / quantityRequired).toInt()
                }
                DiscountConditionApply.ONCE -> {
                    1
                }
            }

            val calculatedQuantity = min(availableToSelect, (quantityGenerated * coefficient).toInt())

            if (calculatedQuantity > 0) {
                bonusProducts[product.id] = calculatedQuantity
                storage.data.input.products[product.id]!!.quantityBonusSelected[discount.id] = calculatedQuantity
            }
        }
        return bonusProducts
    }


    /**
     * Generate bonus points
     * @param discount [DiscountInput] - list of discounts to be applied
     * @param products [HashMap] - list of products in order
     * @return bonusProducts [Int] - list of bonus products to be selected
     */
    private fun generateBonusPoints(
        discount: DiscountIN,
        products: HashMap<Int, Product>
    ): Int {
        var discountBonusPoints = 0
        /*products.forEach {
            discountBonusPoints = it.value.quantity * discount.value.toInt()
        }*/
        bonusPoints += discountBonusPoints
        return discountBonusPoints
    }

    /**
     * Calculate value of discount depend on formula and apply type
     * @param discount [DiscountInput] - list of discounts to be applied
     * @param priceForCalculation [Float] - price for calculation(basic or discounted)
     * @param products [HashMap] - list of products in order
     * @return value [Float] - value of discount
     */
    private fun calculatedValue(
        discount: DiscountIN,
        priceForCalculation: Float,
        products: HashMap<Int, Product>
    ): Float {
        return 0.0F
    }

    /**
     * Create discount to product correspondence map
     * @param inputData [InputData] - all input data
     * @return map [HashMap] - discount to product correspondence map
     */
    fun createMap(inputData: InputData): HashMap<Int, HashMap<Int, Product>> {
        productTotals = hashMapOf()
        orderTotals = Product(0, false, 0, 0.0F)
        for ((productID, product) in inputData.products) {
            productTotals[productID] = Product(
                id = productID,
                quantity = product.quantity,
                amount = product.amount,
                available = product.available
            )

            orderTotals.quantity += product.quantity
            orderTotals.amount += product.amount
        }
        /* println("---")
         println("product totals: " + productTotals.toString())
         println("---")*/

        /*for ((discountID, discount) in inputData.discounts) {
            if (
                discount.products!!.isNotEmpty()) {
                discountProductsMap[discountID] = hashMapOf()
                discountTotals[discountID] =
                    Product(
                        0,
                        0.0F
                    )
            }

            for ((productID, product) in inputData.products) {
                if (discount.products!!.isNotEmpty() && discount.products!!.indexOf(productID) != -1) {
                    discountProductsMap[discountID]?.put(
                        productID,
                        Product(
                            product.quantity,
                            product.amount
                        )
                    )

                    discountTotals[discountID]!!.quantity += product.quantity
                    discountTotals[discountID]!!.amount += product.amount
                }
            }
        }*/
        return discountProductsMap
    }


    fun getMap(): HashMap<Int, HashMap<Int, Product>> {
        return discountProductsMap
    }
}