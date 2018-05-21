package ecalle.com.bmybank.view_holders

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import ecalle.com.bmybank.R
import ecalle.com.bmybank.adapters.ChannelsAdapter
import ecalle.com.bmybank.bo.UserResponse
import ecalle.com.bmybank.extensions.log
import ecalle.com.bmybank.firebase.bo.Channel
import ecalle.com.bmybank.realm.RealmServices
import ecalle.com.bmybank.realm.bo.User
import ecalle.com.bmybank.services.BmyBankApi
import org.jetbrains.anko.find
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Thomas Ecalle on 07/04/2018.
 */
class ChannelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
{
    private val otherUserLastName: TextView = itemView.find(R.id.otherUserLastName)
    private val otherUserFirstName: TextView = itemView.find(R.id.otherUserFirstName)
    private val otherUserImage: ImageView = itemView.find(R.id.otherUserImage)
    private val loader: ProgressBar = itemView.find(R.id.loader)
    private var user: User? = null

    fun bind(channel: Channel, onChannelCLickListener: ChannelsAdapter.OnChannelClickListener)
    {

        itemView.setOnClickListener {
            onChannelCLickListener.onChannelClick(channel, user)
        }

        val currentUser = RealmServices.getCurrentUser(itemView.context)

        val otherUserId = if (currentUser?.id == channel.id_user_1) channel.id_user_2 else channel.id_user_1

        val api = BmyBankApi.getInstance(itemView.context)
        val findUserByIdRequest = api.findUserById(otherUserId)

        findUserByIdRequest.enqueue(object : Callback<UserResponse>
        {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>)
            {
                val userResponse = response.body()
                if (userResponse?.success != null && userResponse?.success)
                {
                    user = userResponse.user
                    otherUserLastName.text = user?.lastname
                    otherUserFirstName.text = user?.firstname

                    otherUserFirstName.visibility = View.VISIBLE
                    otherUserLastName.visibility = View.VISIBLE
                    loader.visibility = View.GONE
                }
            }


            override fun onFailure(call: Call<UserResponse>, t: Throwable)
            {
                log("enable to find user with id $otherUserId")
            }
        })
    }
}