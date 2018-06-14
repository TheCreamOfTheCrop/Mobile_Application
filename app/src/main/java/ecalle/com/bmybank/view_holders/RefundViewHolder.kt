package ecalle.com.bmybank.view_holders

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import ecalle.com.bmybank.R
import ecalle.com.bmybank.realm.bo.Refund
import org.jetbrains.anko.find
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Thomas Ecalle on 07/04/2018.
 */
class RefundViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
{
    private val dateTextView: TextView = itemView.find(R.id.date)
    private val amount: TextView = itemView.find(R.id.amount)

    fun bind(refund: Refund)
    {
        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(refund.creationdate)

        val calendar = Calendar.getInstance()
        calendar.time = date
        dateTextView.text = "${calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())} ${calendar.get(Calendar.YEAR)}"
        amount.text = refund.amount.toString()
    }

}