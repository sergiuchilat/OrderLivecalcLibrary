package order.livecalc.v1.Components.DiscountConditions

import com.merax.livecalc.v1.Storage.DiscountIN
import order.livecalc.v1.Storage.DiscountInput

class CheckCondition {
    private var discount: DiscountIN? = null

    fun checkAll(discountInput: DiscountIN): Boolean {
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