package ecalle.com.bmybank.view_holders

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.storage.FirebaseStorage
import ecalle.com.bmybank.Constants
import ecalle.com.bmybank.R
import ecalle.com.bmybank.adapters.ChannelsAdapter
import ecalle.com.bmybank.extensions.log
import ecalle.com.bmybank.firebase.GlideApp
import ecalle.com.bmybank.firebase.bo.Channel
import ecalle.com.bmybank.realm.RealmServices
import ecalle.com.bmybank.realm.bo.User
import ecalle.com.bmybank.services.BmyBankApi
import ecalle.com.bmybank.services_responses_bo.UserResponse
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
    private val lastMessage: TextView = itemView.find(R.id.lastMessage)
    private val otherUserImage: ImageView = itemView.find(R.id.otherUserImage)
    private val loader: ProgressBar = itemView.find(R.id.loader)
    private var user: User? = null

    fun bind(channel: Channel, onChannelCLickListener: ChannelsAdapter.OnChannelClickListener)
    {

        itemView.setOnClickListener {
            onChannelCLickListener.onChannelClick(channel, user)
        }

        val currentUser = RealmServices.getCurrentUser(itemView.context)

        lastMessage.text = channel.last_message

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
                    loadAvatar()
                }
            }


            override fun onFailure(call: Call<UserResponse>, t: Throwable)
            {
                log("enable to find user with id $otherUserId")
            }
        })
    }

    private fun loadAvatar()
    {
        var emailWithoutSpecialCharacters =user?.email?.replace("@", "")
        emailWithoutSpecialCharacters = emailWithoutSpecialCharacters?.replace(".", "")
        emailWithoutSpecialCharacters = emailWithoutSpecialCharacters.plus(".jpg")


        if (emailWithoutSpecialCharacters != null)
        {
            val firebaseStorage = FirebaseStorage.getInstance()
            val reference = firebaseStorage.reference.child("${Constants.PROFILE_PICTURES_NODE}/$emailWithoutSpecialCharacters")

            // Load the image using Glide
            GlideApp.with(itemView.context)
                    .load(reference)
                    .placeholder(R.drawable.default_profile)
                    .diskCacheStrategy(DiskCacheStrategy.NONE) // <= ADDED
                    .skipMemoryCache(true)
                    .error(R.drawable.default_profile)
                    .into(otherUserImage)

        }
    }
}