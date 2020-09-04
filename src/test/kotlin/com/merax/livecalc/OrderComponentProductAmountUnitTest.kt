package com.merax.livecalc

import order.livecalc.v1.Components.Order
import order.livecalc.v1.Storage.Product
import order.livecalc.v1.Storage.Storage
import org.junit.jupiter.api.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class OrderComponentProductAmountUnitTest {
    private var storage: Storage = Storage()

    @Test
    fun emptyProducts() {
        val orderComponent = Order(storage)
        val products = hashMapOf<Int, Product>()
        val productsSelectedInput = hashMapOf(
            1 to Product(
                id = 1,
                quantity = 0
            )
        )

        val productsSelectedOutput = hashMapOf<Int, Product>()
        val productsAmount = orderComponent.calculateProductAmount(products)

        assertEquals(productsSelectedOutput, productsAmount)
    }

    @Test
    fun zeroQuantitySomeProducts() {
        val orderComponent = Order(storage)
        val products = hashMapOf(
            1 to Product(
                id = 1,
                onlyJuridicalSale = false,
                available = 10,
                price = 1.25F
            ),
            2 to Product(
                id = 2,
                onlyJuridicalSale = false,
                available = 10,
                price = 1.25F
            )
        )

        val productsSelectedInput = hashMapOf(
            1 to Product(
                quantity = 0
            )
        )

        val productsSelectedOutput = hashMapOf<Int, Product>()
        val productsAmount = orderComponent.calculateProductAmount(products)

        assertEquals(productsSelectedOutput, productsAmount)
    }

    @Test
    fun notSelectedQuantity() {
        val orderComponent = Order(storage)
        val products = hashMapOf(
            1 to Product(
                id = 1,
                onlyJuridicalSale = false,
                available = 10,
                price = 1.25F
            )
        )

        val productsSelectedInput = hashMapOf<Int, Product>()

        val productsSelectedOutput = hashMapOf<Int, Product>()
        val productsAmount = orderComponent.calculateProductAmount(products)

        assertEquals(productsSelectedOutput, productsAmount)
    }

    @Test
    fun calculateQuantity() {
        val orderComponent = Order(storage)
        val products = hashMapOf(
            1 to Product(
                id = 1,
                onlyJuridicalSale = false,
                available = 100,
                quantity = 0,
                price = 3.14F
            ),
            2 to Product(
                id = 2,
                onlyJuridicalSale = false,
                quantity = 19,
                available = 100,
                price = 2.53F
            )
        )

        val productsSelectedOutput = hashMapOf<Int, Product>(
            2 to Product(
                id = 2,
                available = 100,
                quantity = 19,
                amount = 48.07F,
                amountDiscounted = 48.07F,
                price = 2.53F,
                priceDiscounted = 2.53F

            )
        )
        val productsAmount = orderComponent.calculateProductAmount(products)

        assertEquals(productsSelectedOutput, productsAmount)
    }
}
