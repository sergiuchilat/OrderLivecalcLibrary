package com.merax.livecalc

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import order.livecalc.v1.Calculator
import order.livecalc.v1.Components.InputAdaptor
import org.junit.Assert

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.merax.livecalc.test", appContext.packageName)
    }

    fun onLoad() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val text = appContext.resources.openRawResource(R.raw.livecalc_input)
            .bufferedReader().use { it.readText() }

        val inputData = InputAdaptor().fromJSON(text)

        val calculator = Calculator()
        calculator.setAPIData(inputData)
        //val result = calculator.getResult().discounts
        val result = inputData.discounts
        Log.d("test", result.toString())
        Assert.assertEquals("com.merax.distributionapp", appContext.packageName)
    }
}
