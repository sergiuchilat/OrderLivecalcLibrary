package order.livecalc.v1.Components

import java.math.RoundingMode

class Utils{
    fun roundDown(value: Float, decimals: Int): Float = (value).toBigDecimal().setScale(decimals, RoundingMode.DOWN).toFloat()
    fun roundUp(value: Float, decimals: Int): Float = (value).toBigDecimal().setScale(decimals, RoundingMode.UP).toFloat()
}