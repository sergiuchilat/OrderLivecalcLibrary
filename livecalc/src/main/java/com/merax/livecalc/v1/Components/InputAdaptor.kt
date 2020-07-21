package order.livecalc.v1.Components

import com.google.gson.Gson
import order.livecalc.v1.Storage.InputData

class InputAdaptor {
    fun fromJSON(jsonString: String): InputData {
        return Gson().fromJson(jsonString, InputData::class.java)
    }
}