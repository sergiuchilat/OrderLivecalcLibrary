package com.merax.livecalc.v1.Storage.discounts

abstract class Discount(
    //Скидочные товары выделяються в отдельную заявку с суммой ноль
    val divide: Boolean,
    //скидка применяется на все товары из заявки, или только на те что есть в акции(в условие и результате)
    val applyOnAllProducts: Boolean
)