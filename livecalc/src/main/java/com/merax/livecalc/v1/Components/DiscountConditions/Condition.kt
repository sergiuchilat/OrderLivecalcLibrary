package order.livecalc.v1.Components.DiscountConditions

class Condition {
    fun check(condition: ICondition):Boolean{
        return condition.check()
    }
}