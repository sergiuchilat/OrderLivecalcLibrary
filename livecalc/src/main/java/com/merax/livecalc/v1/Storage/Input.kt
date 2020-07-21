package order.livecalc.v1.Storage

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import order.livecalc.v1.Storage.DiscountConditions.DiscountConditions
import java.time.temporal.TemporalAmount


data class CashInItem(
    var amount: Float? = 0.0F,
    var method: PaymentMethods? = PaymentMethods.CASH
)

data class CashIn (
    var juridical: CashInItem,
    var physical: CashInItem
)

data class DiscountInput(
    @SerializedName("id")
    @Expose
    var id: Int,

    var canBeApplied: Boolean = true,

    var applied: Boolean = false,

    @SerializedName("apply_conditions")
    @Expose
    var applyConditions: DiscountConditions,

    @SerializedName("apply_to")
    @Expose
    var applyTo: DiscountApplyToType,

    @SerializedName("formula")
    @Expose
    var formula: DiscountFormula,

    @SerializedName("value")
    @Expose
    var value: Float = 0.0F,

    @SerializedName("result_type")
    @Expose
    var resultType: DiscountResultType,

    @SerializedName("combine_with")
    @Expose
    var combineWith: List<Int> ? = listOf(),

    @SerializedName("products")
    @Expose
    var products: List<Int> ? = listOf(),

    @SerializedName("bonus_products")
    @Expose
    var bonusProducts: HashMap<Int, ProductSelected> ? = hashMapOf(),

    @SerializedName("price_to_apply")
    @Expose
    var priceToApply: DiscountPriceToApplyType = DiscountPriceToApplyType.DISCOUNTED_PRICE,

    @SerializedName("zero_order")
    @Expose
    var zeroOrder: Boolean = false
)

data class OrderSettings(
    @SerializedName("split_ratio_juridical")
    @Expose
    var splitRatioJuridical: Float,
    @SerializedName("required_cash_in")
    @Expose
    var requiredCashIn: Float,
    var selectedClientType: ClientType = ClientType.JURIDICAL
)

/*data class SalesHistoryItem(
    @SerializedName("month")
    @Expose
    val month: HashMap<Int, Float>,

    @SerializedName("year")
    @Expose
    val year: HashMap<Int, Float>,

    @SerializedName("all")
    @Expose
    val all: HashMap<Int, Float>
)

data class SalesHistory(
    @SerializedName("store")
    @Expose
    var store: SalesHistoryItem,

    @SerializedName("client")
    @Expose
    var client: SalesHistoryItem
)*/

data class InputData (
    @SerializedName("products")
    @Expose
    var products: HashMap<Int, Product>,

    @SerializedName("discounts")
    @Expose
    var discounts: HashMap<Int, DiscountInput>,

    @SerializedName("order_settings")
    @Expose
    var orderSettings: OrderSettings,

    /*@SerializedName("sales_history")
    @Expose
    var salesHistory: SalesHistory,*/
    @SerializedName("required_cash_in")
    @Expose
    var cashIn: CashIn
)
