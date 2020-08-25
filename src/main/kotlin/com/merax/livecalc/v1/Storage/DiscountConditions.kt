package order.livecalc.v1.Storage.DiscountConditions

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import order.livecalc.v1.Storage.*
import java.sql.Time
import java.util.*

enum class Levels{
    AGENT, STORE, CLIENT, SUBDIVISION, COMPANY, COUNTRY, ALL
}

enum class DiscountCardTypes{
    BRONZE, SILVER, GOLD
}

data class ProductCondition(
    var id: Int,
    var quantity: Int? = 0,
    var amount: Float? = 0.0F,
    var bothConditions: Boolean = false
)

/*
* Discount apply types
* */


/*
* Discount applied on order sum for any product
* */
data class Order (
    var amount: Float = 0.0F
)

data class Group(
    var products: MutableList<ProductCondition> = mutableListOf(),
    var quantity: Int? = 0,
    var amount: Float? = 0.0F,
    var bothConditions: Boolean = false
)

/*
* Current sale conditions
* */
data class CurrentSale(
    var order: Order,
    var productsAnd: MutableList<ProductCondition> = mutableListOf(),
    var productsOr: MutableList<ProductCondition> = mutableListOf(),
    var group: Group
)

data class CashIn(
    var amount: Float,
    var formula: DiscountFormula,
    var paymentType: PaymentMethods
)

data class OrderSettings(
    var clientType: ClientType,
    var shipmentType: ShipmentMethod
)

data class Quotas (
    var max: Int,
    var appliedTo: Levels,
    var interval: Int
)

data class SalesHistory(
    var interval:Int,
    var products: MutableList<Int> = mutableListOf(),
    var salesAmountFrom: Float,
    var salesCountFrom: Int,
    var bothConditions: Boolean,
    var appliedTo: Levels
)

data class ClientSettings(
    var appliedToPriceCategories: List<Int> = listOf(),
    var discountCardType: DiscountCardTypes
)

data class DateTime(
    var startData: Date? = null,
    var endData: Date? = null,
    var startTime: Time? = null,
    var endTime: Time? = null,
    var weekDays: List<Int>? = listOf()
)

data class DiscountConditions (
    @SerializedName("current_sale")
    @Expose
    var currentSale: CurrentSale? = null,
    var orderSettings: OrderSettings? = null,
    var cashIn: CashIn? = null,
    var quotas: Quotas? = null,
    var salesHistory: SalesHistory? = null,
    var clientSettings: ClientSettings? = null,
    var dateTime: DateTime? = null,
    var materials: Int? = null
)