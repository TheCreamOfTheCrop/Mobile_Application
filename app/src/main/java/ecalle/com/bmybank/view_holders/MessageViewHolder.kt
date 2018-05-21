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

/**
 * Created by Thomas Ecalle on 07/04/2018.
 */
class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
{

    fun bind(message: Message, currentUser: User, otherUser: User)
    {
        val otherUserImage: ImageView = itemView.find(R.id.otherUserImage)
        val otherUserText: TextView = itemView.find(R.id.otherUsertext)

        val userImage: ImageView = itemView.find(R.id.userImage)
        val userText: TextView = itemView.find(R.id.userText)


        val currentUserLayout: LinearLayout = itemView.find(R.id.currentUserLayout)
        val otherUserLayout: LinearLayout = itemView.find(R.id.otherUserLayout)

        if (message.id_sender == currentUser.id)
        {
            currentUserLayout.visibility = View.VISIBLE
            otherUserLayout.visibility = View.GONE
            userText.text = message.text
        }
        else
        {
            otherUserLayout.visibility = View.VISIBLE
            currentUserLayout.visibility = View.GONE
            otherUserText.text = message.text
        }
    }
}