package order.livecalc.v1.Components.DiscountConditions

import order.livecalc.v1.Storage.DiscountInput

class CheckCondition {
    private var discount: DiscountInput? = null

    fun checkAll(discountInput: DiscountInput): Boolean {
        discount = discountInput
        return checkCurrentSale()
                && checkOrderSettings()
                && checkCashIn()
                && checkQuotas()
                && checkSalesHistory()
                && checkClientSettings()
                && checkDateTime()
    }

    private fun checkCurrentSale(): Boolean {

        return true
    }

    private fun checkOrderSettings(): Boolean {
        return true
    }

    private fun checkCashIn(): Boolean {
        return true
    }

    private fun checkQuotas(): Boolean {
        return true
    }

    private fun checkSalesHistory(): Boolean {
        return true
    }

    private fun checkClientSettings(): Boolean {
        return true
    }

    private fun checkDateTime(): Boolean {
        return true
    }
}