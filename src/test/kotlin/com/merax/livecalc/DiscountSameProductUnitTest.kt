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
    fun testEmptyDiscounts() {
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
    fun testN_FOR_M_same_not_required_min_quantity() {
        /*********Test 10 + 1: not required min quantity(8) selected -> expected result = null or empty*/
        storage.data.input.discounts[1] = discounts["10+1_same"]!!.copy()
        storage.data.input.products = generateProductVariants(listOf(
            ProductMap(1, 8, 3.23F),
            ProductMap(2, 10, 3.45F),
            ProductMap(3, 10, 3.45F)
        ))
        storage.data.output.discounts = hashMapOf()
        storage.data.input.products[1]?.available = 25

        orderComponent.calculateProductAmount(storage.data.input.products)
        discountComponent.createMap(storage.data.input)
        discountComponent.apply(storage.data.input.discounts)
        assertTrue("10+1: not required min quantity selected", storage.data.output.discounts.isNullOrEmpty())
    }

    @Test
    fun testN_FOR_M_same_expected_1() {
        /********Test 10 + 1: required min quantity(10) selected -> expected result = 1*/
        storage.data.input.discounts[1] = discounts["10+1_same"]!!.copy()
        storage.data.input.products = generateProductVariants(listOf(
            ProductMap(1, 10, 3.23F),
            ProductMap(2, 10, 3.45F),
            ProductMap(3, 10, 3.45F)
        ))
        storage.data.output.discounts = hashMapOf()
        storage.data.input.products[1]?.available = 25

        orderComponent.calculateProductAmount(storage.data.input.products)
        discountComponent.createMap(storage.data.input)
        discountComponent.apply(storage.data.input.discounts)
        assertEquals("10+1: required min quantity(10) selected", 1, storage.data.output.discounts[1]!!.products[1]!!)
    }

    @Test
    fun testN_FOR_M_same_7_plus_3_expected_6() {
        /*******Test 7 + 3: required quantity selected*/
        storage.data.input.discounts[1] = discounts["10+1_same"]!!.copy()
        storage.data.input.discounts[1]?.condition?.content = listOf(DiscountConditionContent(listOf(1), 7.0F))
        storage.data.input.discounts[1]?.result?.content = listOf(DiscountResultContent(1, 3.0F))
        storage.data.input.products = generateProductVariants(listOf(
            ProductMap(1, 15, 3.23F),
            ProductMap(2, 10, 3.45F),
            ProductMap(3, 10, 3.45F)
        ))
        storage.data.output.discounts = hashMapOf()
        storage.data.input.products[1]?.available = 25

        orderComponent.calculateProductAmount(storage.data.input.products)
        discountComponent.createMap(storage.data.input)
        discountComponent.apply(storage.data.input.discounts)
        assertEquals("Test 7 + 3: required quantity selected", 6, storage.data.output.discounts[1]!!.products[1]!!)
    }

    @Test
    fun testN_FOR_M_same_zero_available_to_select() {
        /*******Test 7 + 3: zero quantity available to select*/
        storage.data.input.discounts[1] = discounts["10+1_same"]!!.copy()
        storage.data.input.discounts[1]?.condition?.content = listOf(DiscountConditionContent(listOf(1), 7.0F))
        storage.data.input.discounts[1]?.result?.content = listOf(DiscountResultContent(1, 3.0F))
        storage.data.input.products = generateProductVariants(listOf(
            ProductMap(1, 15, 3.23F),
            ProductMap(2, 10, 3.45F),
            ProductMap(3, 10, 3.45F)
        ))
        storage.data.output.discounts = hashMapOf()
        storage.data.input.products[1]?.available = 15

        orderComponent.calculateProductAmount(storage.data.input.products)
        discountComponent.createMap(storage.data.input)
        discountComponent.apply(storage.data.input.discounts)
        assertTrue("Test 7 + 3: zero quantity available to select", storage.data.output.discounts.isNullOrEmpty())
    }

    @Test
    fun testN_FOR_M_same_only_4_available_from_6_required() {
        /*******Test 7 + 3: only 4 available to select from 6 required*/
        storage.data.input.discounts[1] = discounts["10+1_same"]!!.copy()
        storage.data.input.discounts[1]?.condition?.content = listOf(DiscountConditionContent(listOf(1), 7.0F))
        storage.data.input.discounts[1]?.result?.content = listOf(DiscountResultContent(1, 3.0F))
        storage.data.input.products = generateProductVariants(listOf(
            ProductMap(1, 15, 3.23F),
            ProductMap(2, 10, 3.45F),
            ProductMap(3, 10, 3.45F)
        ))
        storage.data.output.discounts = hashMapOf()
        storage.data.input.products[1]?.available = 19

        orderComponent.calculateProductAmount(storage.data.input.products)
        discountComponent.createMap(storage.data.input)
        discountComponent.apply(storage.data.input.discounts)
        assertEquals("Test 7 + 3: only 4 available to select from 6 required", 4, storage.data.output.discounts[1]!!.products[1]!!)
    }

    @Test
    fun testN_FOR_M_2_discounts() {
        /*******Test 10 + 1: apply 2 discounts on same product*/
        storage.data.input.discounts[1] = discounts["10+1_same"]!!.copy()
        storage.data.input.discounts[2] = discounts["10+1_same"]!!.copy(id = 2)

        storage.data.input.products = generateProductVariants(listOf(
            ProductMap(1, 10, 3.23F),
            ProductMap(2, 10, 3.45F),
            ProductMap(3, 10, 3.45F)
        ))
        storage.data.output.discounts = hashMapOf()
        storage.data.input.products[1]?.available = 12

        orderComponent.calculateProductAmount(storage.data.input.products)
        discountComponent.createMap(storage.data.input)
        discountComponent.apply(storage.data.input.discounts)
        assertEquals("Test 10 + 1: apply 2 discounts on same product", 2, storage.data.output.discounts[1]!!.products[1]!! + storage.data.output.discounts[2]!!.products[1]!!)
    }

    @Test
    fun testN_FOR_M_2_discounts_not_enough_forD2() {
        /*******Test 10 + 1: 2 discount on same product, not enough products for second discount*/
        storage.data.input.discounts[1] = discounts["10+1_same"]!!.copy()
        storage.data.input.discounts[2] = discounts["10+1_same"]!!.copy(id = 2)

        storage.data.input.products = generateProductVariants(listOf(
            ProductMap(1, 10, 3.23F),
            ProductMap(2, 10, 3.45F),
            ProductMap(3, 10, 3.45F)
        ))
        storage.data.output.discounts = hashMapOf()
        storage.data.input.products[1]?.available = 11

        orderComponent.calculateProductAmount(storage.data.input.products)
        discountComponent.createMap(storage.data.input)
        discountComponent.apply(storage.data.input.discounts)
        assertNull("Test 10 + 1: 2 discount on same product, not enough products for second discount", storage.data.output.discounts[2])
        assertEquals("Test 10 + 1: 2 discount on same product, not enough products for second discount", 1, storage.data.output.discounts[1]!!.products[1]!!)
    }
}