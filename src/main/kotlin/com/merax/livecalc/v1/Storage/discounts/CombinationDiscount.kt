package com.merax.livecalc.v1.Storage.discounts

//Скидка для вариаций 10+1, 2+1=5, 5+1(где 1 может быть выбран из нескольки вариантов)
class CombinationDiscount(
    divide: Boolean,
    applyOnAllProducts: Boolean,
    //ID товара и минимум сколько нужно купить что-бы скидка применилась
    val conditionProducts: Map<Int, Int>,
    //Бонус который дается если условие выполнено "или"
    val orResultProducts: OrResultProducts?,
    //При условие "и" - статично выдает товары какие можно получить как бонус
    val andResultProducts: Map<Int, Int>
) : Discount(
    divide,
    applyOnAllProducts
)

data class OrResultProducts(
    //При условие "или" указывает сколько бонусных товаров может взять
    val maxQuantity: Int = 0,
    //При условие "или" указывает какие товары можно выбрать
    val maxQuantityProducts: List<Int> = listOf()
)
