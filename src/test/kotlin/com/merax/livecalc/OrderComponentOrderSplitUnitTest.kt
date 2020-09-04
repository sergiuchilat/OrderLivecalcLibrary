package com.merax.livecalc

import order.livecalc.v1.Components.Order
import order.livecalc.v1.Storage.*
import org.junit.jupiter.api.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class OrderComponentOrderSplitUnitTest {
    private var storage: Storage = Storage()
    private var orderComponent = Order(storage)

    init {
        initDataProvider()
    }

    private fun initDataProvider(){
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
    fun emptyProducts() {
        orderComponent.split()
        assertEquals(Orders(), storage.data.output.orders)
    }

    @Test
    fun splitRatioJuridicalIsZero(){
        storage.data.input.products = hashMapOf(
            1 to Product(
                id = 1,
                onlyJuridicalSale = false,
                available = 10,
                price = 1.25F
            )
        )

        storage.data.output.products = hashMapOf(
            1 to Product(
                id = 1,
                quantity = 5
            )
        )

        storage.data.input.orderSettings.splitRatioJuridical = 0.0F
        storage.data.input.orderSettings.selectedClientType = ClientType.PHYSICAL

        storage.setProductsQuantity(
            orderComponent.calculateProductAmount(storage.data.input.products)
        )
        orderComponent.split()
        val ordersAfter = Orders(
            basic = storage.data.output.orders.basic,
            additional = storage.data.output.orders.additional
        )
        assertNull(ordersAfter.zeroPrice)
    }

    @Test
    fun emptyZeroProducts(){
        storage.data.output.zeroOrderProducts = hashMapOf()
        orderComponent.split()
        assertEquals(null, storage.data.output.orders.zeroPrice)
    }

    @Test
    fun basicOrderIsJuridical(){

        storage.data.input.products = hashMapOf(
            1 to Product(
                id = 1,
                onlyJuridicalSale = false,
                available = 10,
                price = 1.25F,
                quantity = 5
            )
        )

        storage.data.output.products = hashMapOf(
            1 to Product(
                quantity = 5,
                available = 10
            )
        )

        storage.data.input.orderSettings.splitRatioJuridical = 2.0F
        storage.data.input.orderSettings.selectedClientType = ClientType.JURIDICAL

        storage.setProductsQuantity(
            orderComponent.calculateProductAmount(storage.data.input.products)
        )
        orderComponent.split()
        val ordersAfter = Orders(
            basic = storage.data.output.orders.basic,
            additional = storage.data.output.orders.additional
        )
        assertNull(ordersAfter.additional)
        assertNull(ordersAfter.zeroPrice)
    }
}
