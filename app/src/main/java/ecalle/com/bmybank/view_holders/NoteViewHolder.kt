package ecalle.com.bmybank.view_holders

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import ecalle.com.bmybank.Constants
import ecalle.com.bmybank.ProfileViewerActivity
import ecalle.com.bmybank.R
import ecalle.com.bmybank.firebase.GlideApp
import ecalle.com.bmybank.realm.bo.Note
import ecalle.com.bmybank.services.BmyBankApi
import ecalle.com.bmybank.services_responses_bo.UserResponse
import org.jetbrains.anko.find
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Thomas Ecalle on 07/04/2018.
 */
class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener
{


    private val image = itemView.find<CircleImageView>(R.id.image)
    private val firstName = itemView.find<TextView>(R.id.firstName)
    private val lastName = itemView.find<TextView>(R.id.lastName)
    private val noteTextView = itemView.find<TextView>(R.id.note)
    private val comment = itemView.find<TextView>(R.id.comment)
    private var requesterId: Int? = null

    fun bind(note: Note)
    {
        requesterId = note.user_requester_id
        comment.text = note.comments
        noteTextView.text = note.note.toString()

        image.setOnClickListener(this)

        findUserInformations(note.user_requester_id)
    }

    override fun onClick(view: View?)
    {
        when (view?.id)
        {
            image.id ->
            {
                if (requesterId == null) return

                val intent = Intent(itemView.context, ProfileViewerActivity::class.java)
                intent.putExtra(ProfileViewerActivity.USER_ID_KEY, requesterId!!)

                intent.putExtra(ProfileViewerActivity.USER_FIRSTNAME_KEY, firstName.text)

                itemView.context.startActivity(intent)
            }
        }

    }

    private fun findUserInformations(userId: Int)
    {
        val api = BmyBankApi.getInstance(itemView.context)
        val findUserByIdRequest = api.findUserById(userId)


        findUserByIdRequest.enqueue(object : Callback<UserResponse>
        {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>)
            {
                val userResponse = response.body()
                if (userResponse?.success != null && userResponse?.success)
                {
                    val user = userResponse.user

                    firstName.text = user.firstname
                    lastName.text = user.lastname

                    var emailWithoutSpecialCharacters = user.email.replace("@", "")
                    emailWithoutSpecialCharacters = emailWithoutSpecialCharacters.replace(".", "")
                    emailWithoutSpecialCharacters = emailWithoutSpecialCharacters.plus(".jpg")


                    if (emailWithoutSpecialCharacters != null)
                    {
                        val firebaseStorage = FirebaseStorage.getInstance()
                        val reference = firebaseStorage.reference.child("${Constants.PROFILE_PICTURES_NODE}/$emailWithoutSpecialCharacters")

                        // Load the image using Glide
                        GlideApp.with(itemView.context)
                                .load(reference)
                                .placeholder(R.drawable.default_profile)
                                .error(R.drawable.default_profile)
                                .into(image)
                    }

                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable)
            {

            }
        })

    }

}