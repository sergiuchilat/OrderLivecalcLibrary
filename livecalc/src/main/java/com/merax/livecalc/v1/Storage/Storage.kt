package order.livecalc.v1.Storage

import android.util.Log

class Storage {
    var data: StorageData = StorageData(
        input = InputData(
            products = hashMapOf(),
            discounts = hashMapOf(),
            orderSettings = OrderSettings(
                splitRatioJuridical = 0.0F,
                requiredCashIn = 0.0F
            ),
            cashIn = CashIn(
                juridical = CashInItem(),
                physical = CashInItem()
            )
        ),
        output = OutputData(
            products = hashMapOf(),
            discounts = hashMapOf(),
            orders = Orders(
                basic = null,
                additional = null,
                zeroPrice = null
            ),
            writeOff = WriteOff(
                official = listOf(),
                unofficial = listOf()
            ),
            cash = Cash(
                juridical = 0.0F,
                physical = 0.0F
            ),
            bonusPoints = 0,
            requiredCashIn = Cash(
                juridical = 0.0F,
                physical = 0.0F
            ),
            orderSettings = OrderParams(
                clientType = ClientType.JURIDICAL
            )
        )
    )

    fun setAPIData(inputData: InputData){
        //Log.d("test", data.toString())
        data.input = inputData
    }

    fun setCashIn(cash: CashIn){
        data.input.cashIn = cash
    }

    fun setProductsQuantity(products: HashMap <Int, ProductSelected>){
        data.output.products = products
    }

    fun setOutputOrders(orders: Orders){
        data.output.orders = orders
    }
    fun setOutputDiscounts(discounts: HashMap<Int, DiscountOutput>){
        data.output.discounts = discounts
    }

    fun setOutputCash(cash: Cash){
        data.output.cash = cash
    }

    fun setWriteOff(writeOff: WriteOff){
        data.output.writeOff = writeOff
    }

    fun setBonusPoints(bonusPoints: Int){
        data.output.bonusPoints = bonusPoints
    }

    fun getInputData():InputData{
        return data.input
    }


   /* fun getOutputData():OutputData{
        return data!!.output
    }*/



    fun getOutputData():OutputData{
        return OutputData(
            products = data.output.products,
            orders = data.output.orders,
            discounts = data.output.discounts,
            cash = data.output.cash,
            writeOff = data.output.writeOff,
            bonusPoints = data.output.bonusPoints,
            requiredCashIn = data.output.requiredCashIn,
            orderSettings = data.output.orderSettings
        )
    }
}