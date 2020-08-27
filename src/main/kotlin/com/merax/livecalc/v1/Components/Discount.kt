package order.livecalc.v1.Components

import order.livecalc.v1.Components.DiscountConditions.CheckCondition
import order.livecalc.v1.Storage.*

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

    fun init(discounts: HashMap<Int, DiscountInput>) {
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
    private fun filterByClientSettings(discount: DiscountInput):Boolean{
        return true
    }


    /**
     * Apply list of discounts
     * @param discounts [HashMap] - list of discounts to be applied
     * @param products [HashMap] - list of products in order
     */
    fun apply(discounts: HashMap<Int, DiscountInput>, products: HashMap<Int, Product>) {
        for ((_, discount) in discounts) {
            if (!this.canApply(discount))
                continue
            this.apply(discount, products)
        }
        System.out.println("Applied discounts:" + appliedDiscounts.toString())
        storage.data.output.discounts = appliedDiscounts
    }

    /**
     * Check if one discount can be applied
     * @param discount[DiscountInput] - discount item
     * @return canBeApplied [Boolean]
     */

    private fun canApply(discount: DiscountInput) =
        canBeCombined(discount) && checkApplyCondition(discount)

    /**
     * Check if discount condition satisfy requirements
     * @param discount[DiscountInput] - discount item
     * @return checkApplyCondition [Boolean]
     * */
    private fun checkApplyCondition(discount: DiscountInput): Boolean =
        checkCondition.checkAll(discount)

    /**
     * Check if discount can be combined with previously applied discounts
     * @param discount[DiscountInput] - discount item
     * @return canBeCombined [Boolean]
     * */
    private fun canBeCombined(discount: DiscountInput) =
        discount.combineWith!!.isEmpty() || appliedDiscounts.isEmpty() || (discount.combineWith?.intersect(
            appliedDiscounts.keys
        ))!!.isNotEmpty()

    /**
     * Apply one of discounts
     * @param discount [DiscountInput] - discount item
     * @param products [HashMap] - list of products in order
     *
     */
    private fun apply(discount: DiscountInput, products: HashMap<Int, Product>) {
        var discountSum = 0.0F
        var discountBonusPoints = 0
        var discountProducts: HashMap<Int, Int> = hashMapOf()
        val discountProductsToSelect: HashMap<Int, Int> = hashMapOf()
        when (discount.resultType) {
            DiscountResultType.AMOUNT -> discountSum = this.calculateBonusSum(discount, products)
            DiscountResultType.PRODUCTS_SAME -> discountProducts =
                this.generateBonusProducts(discount, products)
            /*DiscountResultType.PRODUCTS_OTHER -> discountProductsToSelect =
                this.generateBonusProductsToSelect(discount)*/
            DiscountResultType.POINTS -> discountBonusPoints =
                this.generateBonusPoints(discount, products)
        }

        this.appliedDiscounts[discount.id] =
            DiscountOutput(
                discount.id,
                discountSum,
                discountProducts,
                discountProductsToSelect,
                discountBonusPoints
            )
        //Log.d("livecalc", zeroOrderProducts.toString())
    }

    /**
     * Calculate bonus sum
     * @param discount [DiscountInput] - list of discounts to be applied
     * @param products [HashMap] - list of products in order
     * @return discountSum [Float] - discount sum
     */
    private fun calculateBonusSum(
        discount: DiscountInput,
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
     * @param products [HashMap] - list of products in order
     * @return bonusProducts [HashMap] - list of bonus products
     */
    private fun generateBonusProducts(
        discount: DiscountInput,
        products: HashMap<Int, Product>
    ): HashMap<Int, Int> {
        println(products.toString())
        val bonusProducts: HashMap<Int, Int> = hashMapOf()
        for ((productID, product) in products) {
            val bonusQuantity = (product.quantity * discount.value).toInt()
            bonusProducts[productID] = bonusQuantity
            if (discount.zeroOrder) {
                zeroOrderProducts[productID] = bonusQuantity
            } else {
                product.quantity += bonusQuantity
                product.priceDiscounted = product.amountDiscounted / product.quantity
            }
        }
        return bonusProducts
    }

    /**
     * Generate bonus products to select
     * @param discount [DiscountInput] - list of discounts to be applied
     * @return bonusProducts [HashMap] - list of bonus products to be selected
     */
    //Todo implement OtherProducts algorithm
    private fun generateBonusProductsToSelect(discount: DiscountInput): HashMap<Int, Int> {
        val bonusProducts: HashMap<Int, Int> = hashMapOf()
        discount.bonusProducts!!.forEach {
            bonusProducts[it.key] = (13 * discount.value).toInt()
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
        discount: DiscountInput,
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
        discount: DiscountInput,
        priceForCalculation: Float,
        products: HashMap<Int, Product>
    ): Float {
        return if (discount.formula == DiscountFormula.PERCENT) {
            priceForCalculation * discount.value / 100
        } else {
            priceForCalculation - when (discount.applyTo) {
                DiscountApplyToType.ORDER -> Utils().roundDown(discount.value / products.size, 2)
                DiscountApplyToType.EACH_PRODUCT -> discount.value
                DiscountApplyToType.EACH_PRODUCT_IN_CONDITION -> discount.value//Todo apply only for products in condition
            }
        }
    }

    /**
     * Create discount to product correspondence map
     * @param inputData [InputData] - all input data
     * @return map [HashMap] - discount to product correspondence map
     */
    fun createMap(inputData: InputData): HashMap<Int, HashMap<Int, Product>> {
        /*for ((productID, product) in inputData.products) {
            productTotals[productID] = Product(
                product.quantity,
                product.amount
            )

            orderTotals.quantity += product.quantity
            orderTotals.amount += product.amount
        }


        for ((discountID, discount) in inputData.discounts) {
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
        }
        *//*Log.d("livecalc", discountProductsMap.toString())
        Log.d("livecalc", discountProductTotals.toString())
        Log.d("livecalc", "after")*/
        return discountProductsMap
    }


    fun getMap(): HashMap<Int, HashMap<Int, Product>> {
        return discountProductsMap
    }
}