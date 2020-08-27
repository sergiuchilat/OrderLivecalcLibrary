package com.merax.livecalc

import order.livecalc.v1.Calculator
import order.livecalc.v1.Components.Discount
import order.livecalc.v1.Components.Order
import order.livecalc.v1.Storage.*
import order.livecalc.v1.Storage.CashIn
import order.livecalc.v1.Storage.DiscountConditions.*
import order.livecalc.v1.Storage.OrderSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.jupiter.api.Test


class DiscountSameProductUnitTest {
    private var storage: Storage = Storage()
    private var orderComponent = Order(storage)
    private var discountComponent = Discount(storage)

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

    @Test
    fun emptyDiscounts() {
        storage.data.input.products = hashMapOf(
            1 to Product(
                id = 1,
                onlyJuridicalSale = false,
                available = 10,
                price = 1.25F
            )
        )
        val productsSelectedInput = hashMapOf(
            1 to Product(
                id = 1,
                quantity = 2
            )
        )

        val emptyDiscounts: HashMap<Int, DiscountOutput> = hashMapOf()
        orderComponent.calculateProductAmount(storage.data.input.products, productsSelectedInput)
        discountComponent.createMap(storage.getInputData())
        discountComponent.apply(storage.getInputData().discounts, storage.getInputData().products)
        System.out.println(storage.data.output.discounts)
        assertEquals(emptyDiscounts, storage.data.output.discounts)
    }

    @Test
    fun oneDiscount() {
        storage.data.input.products = hashMapOf(
            1 to Product(
                id = 1,
                onlyJuridicalSale = false,
                available = 10,
                price = 1.25F,
                quantity = 6
            )
        )

        val selected: Float = 10.0F;
        val bonusDelta = 3;
        val bonus = 2;

        storage.data.input.discounts = hashMapOf(
            1 to DiscountInput(
                id = 1,
                canBeApplied = true,
                applied = false,
                applyConditions = DiscountConditions(

                ),
                applyTo = DiscountApplyToType.EACH_PRODUCT,
                formula = DiscountFormula.VALUE,
                resultType = DiscountResultType.PRODUCTS_SAME,
                value = selected / bonusDelta * bonus,
                combineWith = listOf(),
                products = listOf(),
                bonusProducts = hashMapOf(),
                priceToApply = DiscountPriceToApplyType.BASIC_PRICE,
                zeroOrder = false
            )
        )

        val emptyDiscounts: HashMap<Int, DiscountOutput> = hashMapOf()
        orderComponent.calculateProductAmount(storage.data.input.products, storage.data.input.products)
        discountComponent.createMap(storage.getInputData())
        discountComponent.apply(storage.getInputData().discounts, storage.data.input.products)
        System.out.println(storage.data.output.discounts)
        //assertEquals(emptyDiscounts, storage.data.output.discounts)
    }

}