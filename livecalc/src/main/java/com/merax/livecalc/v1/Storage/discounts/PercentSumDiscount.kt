package com.merax.livecalc.v1.Storage.discounts

class PercentSumDiscount(
    divide: Boolean,
    applyOnAllProducts: Boolean,
    val conditionProducts: Map<Int, Int>,
    val resultProducts: List<Int>
) : Discount(
    divide,
    applyOnAllProducts
)