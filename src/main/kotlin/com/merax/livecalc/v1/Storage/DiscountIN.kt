package com.merax.livecalc.v1.Storage
/*
* Discount was applied for quantity/amount of selected products
* */
enum class DiscountConditionTypes{
    QUANTITY, AMOUNT
}
/*
* Discount will be applied for each goup of quantity/amount or one per order
* */
enum class DiscountConditionApply{
    FOREACH, ONCE
}
/*
* id = productId(list of products in condition to check), value = quantity/amount
* */
data class DiscountConditionContent(
    var id: List<Int> = listOf(),
    var value: Float = 0.0F
)
/*
* How discount will be applied
* */
data class DiscountCondition(
    var type: DiscountConditionTypes = DiscountConditionTypes.QUANTITY,
    var apply: DiscountConditionApply = DiscountConditionApply.ONCE,
    var content: List<DiscountConditionContent>
)
/*
* If discount match condition, will generate new products, percent of amount (-price) or percent to reuse for other products
* */
enum class DiscountResultTypes{
    PRODUCTS, PERCENT, PERCENT_REUSE, PRODUCTS_ZERO, POINTS
}
/*
* AND - all selected will be returned, OR - can select one of returned
* */
enum class DiscountResultMatch{
    AND, OR
}
/*
* id = productId, value = quantity/amount
* */
data class DiscountResultContent(
    var id: Int = 0,
    var value: Float = 0.0F
)
/*
* What discount generate
* */
data class DiscountResult(
    var type: DiscountResultTypes = DiscountResultTypes.PRODUCTS,
    var match: DiscountResultMatch = DiscountResultMatch.AND,
    var content: List<DiscountResultContent> = listOf()
)

data class DiscountIN(
    var id: Int = 0,
    var canBeApplied: Boolean = true,
    var condition: DiscountCondition? = null,
    var result: DiscountResult? = null
)