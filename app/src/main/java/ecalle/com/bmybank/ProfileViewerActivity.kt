package ecalle.com.bmybank

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.TextView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import ecalle.com.bmybank.adapters.LoansAdapter
import ecalle.com.bmybank.adapters.NotesAdapter
import ecalle.com.bmybank.firebase.GlideApp
import ecalle.com.bmybank.realm.bo.User
import ecalle.com.bmybank.services.BmyBankApi
import ecalle.com.bmybank.services_responses_bo.NoteListReponse
import ecalle.com.bmybank.services_responses_bo.UserResponse
import kotlinx.android.synthetic.main.activity_profile_viewer.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.find
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Thomas Ecalle on 07/07/2018.
 */
class ProfileViewerActivity : AppCompatActivity(), ToolbarManager
{
    companion object
    {
        const val USER_ID_KEY = "userIdKey"
        const val USER_FIRSTNAME_KEY = "userFirstNameKey"
        const val COLOR_KEY = "colorKey"
    }

    private var user: User? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var notesAdapter: NotesAdapter

    private lateinit var firstName: TextView
    private lateinit var lastName: TextView
    private lateinit var image: CircleImageView

    override val toolbar by lazy { find<Toolbar>(R.id.toolbar) }


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_viewer)

        recyclerView = find(R.id.recyclerView)
        firstName = find(R.id.firstName)
        lastName = find(R.id.lastName)
        image = find(R.id.image)

        enableHomeAsUp { onBackPressed() }


        if (intent != null && intent.getIntExtra(ProfileViewerActivity.USER_ID_KEY, -1) != -1)
        {
            val userId = intent.getIntExtra(ProfileViewerActivity.USER_ID_KEY, -1)

            toolbarTitle = intent.getStringExtra(USER_FIRSTNAME_KEY) ?: ""

            changeColor(LoansAdapter.Color.BLUE, this)

            findUserInformations(userId)
            findUserNotes(userId)

        }

    }

    private fun findUserNotes(userId: Int)
    {

        val api = BmyBankApi.getInstance(ctx)
        val findUserNotesRequest = api.getNotes(userId)

        findUserNotesRequest.enqueue(object : Callback<NoteListReponse>
        {
            override fun onResponse(call: Call<NoteListReponse>, response: Response<NoteListReponse>)
            {
                val listResponse = response.body()
                if (listResponse?.success != null && listResponse?.success)
                {
                    val notesList = listResponse.note

                    notesLoader.visibility = View.GONE

                    if (notesList.isEmpty())
                    {
                        recyclerView.visibility = View.GONE
                        emptyNotes.visibility = View.VISIBLE
                    }
                    else
                    {
                        recyclerView.visibility = View.VISIBLE
                        emptyNotes.visibility = View.GONE

                        notesAdapter = NotesAdapter(notesList)
                        recyclerView.layoutManager = LinearLayoutManager(ctx)
                        recyclerView.adapter = notesAdapter
                    }

                }
            }


            override fun onFailure(call: Call<NoteListReponse>, t: Throwable)
            {
                toast(R.string.not_internet)
            }
        })

    }

    private fun findUserInformations(userId: Int)
    {
        val api = BmyBankApi.getInstance(ctx)
        val findUserByIdRequest = api.findUserById(userId)


        findUserByIdRequest.enqueue(object : Callback<UserResponse>
        {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>)
            {
                val userResponse = response.body()
                if (userResponse?.success != null && userResponse?.success)
                {
                    user = userResponse.user

                    toolbarTitle = if (user?.firstname != null) user?.firstname!! else ""
                    firstName.text = user?.firstname
                    lastName.text = user?.lastname
                    ratingBar.rating = 3f

                    var emailWithoutSpecialCharacters = user?.email?.replace("@", "")
                    emailWithoutSpecialCharacters = emailWithoutSpecialCharacters?.replace(".", "")
                    emailWithoutSpecialCharacters = emailWithoutSpecialCharacters.plus(".jpg")


                    if (emailWithoutSpecialCharacters != null)
                    {
                        val firebaseStorage = FirebaseStorage.getInstance()
                        val reference = firebaseStorage.reference.child("${Constants.PROFILE_PICTURES_NODE}/$emailWithoutSpecialCharacters")

                        // Load the image using Glide
                        GlideApp.with(ctx)
                                .load(reference)
                                .placeholder(R.drawable.default_profile)
                                .error(R.drawable.default_profile)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .error(R.drawable.default_profile)
                                .into(image)
                    }

                    removeLoaderOnNames()
                }
            }


            override fun onFailure(call: Call<UserResponse>, t: Throwable)
            {
                toast(R.string.not_internet)
            }
        })

    }

    private fun removeLoaderOnNames()
    {
        namesLoader.visibility = View.GONE
        firstName.visibility = View.VISIBLE
        lastName.visibility = View.VISIBLE
    }
}