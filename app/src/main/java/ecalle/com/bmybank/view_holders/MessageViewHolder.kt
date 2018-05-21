package ecalle.com.bmybank.view_holders

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import ecalle.com.bmybank.R
import ecalle.com.bmybank.firebase.bo.Message
import ecalle.com.bmybank.realm.bo.User
import org.jetbrains.anko.find
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Thomas Ecalle on 07/04/2018.
 */
class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
{

    fun bind(message: Message, currentUser: User, otherUser: User)
    {
        val otherUserImage: ImageView = itemView.find(R.id.otherUserImage)
        val otherUserText: TextView = itemView.find(R.id.otherUsertext)
        val otherDate: TextView = itemView.find(R.id.otherDate)


        val userImage: ImageView = itemView.find(R.id.userImage)
        val userText: TextView = itemView.find(R.id.userText)
        val date: TextView = itemView.find(R.id.date)



        val currentUserLayout: LinearLayout = itemView.find(R.id.currentUserLayout)
        val otherUserLayout: LinearLayout = itemView.find(R.id.otherUserLayout)


        val cal = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE)
        cal.time = sdf.parse(message.date)

        val dateString = "Le ${cal.get(Calendar.DAY_OF_MONTH)}/${cal.get(Calendar.MONTH)}/${cal.get(Calendar.YEAR)} " +
                "à ${cal.get(Calendar.HOUR_OF_DAY)}h${cal.get(Calendar.MINUTE)}"


        if (message.id_sender == currentUser.id)
        {
            currentUserLayout.visibility = View.VISIBLE
            otherUserLayout.visibility = View.GONE
            userText.text = message.text
            date.text = dateString
        }
        else
        {
            otherUserLayout.visibility = View.VISIBLE
            currentUserLayout.visibility = View.GONE
            otherUserText.text = message.text
            otherDate.text = dateString
        }
    }
}