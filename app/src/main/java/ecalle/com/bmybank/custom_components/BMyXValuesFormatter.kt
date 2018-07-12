package ecalle.com.bmybank.custom_components

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import java.text.DateFormatSymbols

/**
 * Created by Thomas Ecalle on 11/07/2018.
 */
class BMyXValuesFormatter(val list: MutableList<Int>) : IAxisValueFormatter
{

    override fun getFormattedValue(value: Float, axis: AxisBase?): String
    {
        val dateFormatSymbols = DateFormatSymbols()
        return dateFormatSymbols.months[list[value.toInt()]]
    }

    fun getDecimalDigits(): Int
    {
        return 0
    }
}