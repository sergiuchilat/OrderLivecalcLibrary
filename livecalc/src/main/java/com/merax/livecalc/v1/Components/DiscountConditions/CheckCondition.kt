package order.livecalc.v1.Components.DiscountConditions

import android.util.Log
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
        Log.d("test", "checkCurrentSale")
        Log.d("test", discount.toString())

        return true
    }

    private fun checkOrderSettings(): Boolean {
        Log.d("test", "checkOrderSettings")
        return true
    }

    private fun checkCashIn(): Boolean {
        Log.d("test", "checkCashIn")
        return true
    }

    private fun checkQuotas(): Boolean {
        Log.d("test", "checkQuotas")
        return true
    }

    private fun checkSalesHistory(): Boolean {
        Log.d("test", "checkSalesHistory")
        return true
    }

    private fun checkClientSettings(): Boolean {
        Log.d("test", "checkClientSettings")
        return true
    }

    private fun checkDateTime(): Boolean {
        Log.d("test", "checkDateTime")
        return true
    }
}