package com.merax.livecalc

import order.livecalc.v1.Components.Order
import order.livecalc.v1.Storage.Product
import order.livecalc.v1.Storage.ProductSelected
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
            1 to ProductSelected(
                quantity = 0
            )
        )

        val productsSelectedOutput = hashMapOf<Int, ProductSelected>()
        val productsAmount = orderComponent.calculateProductAmount(products, productsSelectedInput)

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
            1 to ProductSelected(
                quantity = 0
            )
        )

        val productsSelectedOutput = hashMapOf<Int, ProductSelected>()
        val productsAmount = orderComponent.calculateProductAmount(products, productsSelectedInput)

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

        val productsSelectedInput = hashMapOf<Int, ProductSelected>()

        val productsSelectedOutput = hashMapOf<Int, ProductSelected>()
        val productsAmount = orderComponent.calculateProductAmount(products, productsSelectedInput)

        assertEquals(productsSelectedOutput, productsAmount)
    }

    @Test
    fun calculateQuantity() {
        val orderComponent = Order(storage)
        val products = hashMapOf(
            1 to Product(
                id = 1,
                onlyJuridicalSale = false,
                available = 10,
                price = 3.14F
            ),
            2 to Product(
                id = 2,
                onlyJuridicalSale = false,
                available = 10,
                price = 2.53F
            )
        )

        val productsSelectedInput = hashMapOf<Int, ProductSelected>(
            1 to ProductSelected(
                quantity = 0
            ),
            2 to ProductSelected(
                quantity = 19
            )
        )

        val productsSelectedOutput = hashMapOf<Int, ProductSelected>(
            2 to ProductSelected(
                quantity = 19,
                amount = 48.07F,
                amountDiscounted = 48.07F,
                price = 2.53F,
                priceDiscounted = 2.53F

            )
        )
        val productsAmount = orderComponent.calculateProductAmount(products, productsSelectedInput)

        assertEquals(productsSelectedOutput, productsAmount)
    }
}
