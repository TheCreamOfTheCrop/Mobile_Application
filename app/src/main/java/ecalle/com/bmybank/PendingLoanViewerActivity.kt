package ecalle.com.bmybank

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.*
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import ecalle.com.bmybank.adapters.LoansAdapter
import ecalle.com.bmybank.custom_components.BeMyDialog
import ecalle.com.bmybank.extensions.customAlert
import ecalle.com.bmybank.firebase.GlideApp
import ecalle.com.bmybank.firebase.Utils
import ecalle.com.bmybank.firebase.bo.Channel
import ecalle.com.bmybank.firebase.bo.ListMessages
import ecalle.com.bmybank.firebase.bo.Message
import ecalle.com.bmybank.fragments.MyLoansFragment
import ecalle.com.bmybank.fragments.PublicLoansFragment
import ecalle.com.bmybank.realm.RealmServices
import ecalle.com.bmybank.realm.bo.Loan
import ecalle.com.bmybank.realm.bo.User
import ecalle.com.bmybank.services.BmyBankApi
import ecalle.com.bmybank.services_responses_bo.UserResponse
import kotlinx.android.synthetic.main.activity_loan_viewer.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.find
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Thomas Ecalle on 10/04/2018.
 */
class PendingLoanViewerActivity : AppCompatActivity(), ToolbarManager, View.OnClickListener
{


    companion object
    {
        val USER_KEY = "userKey"
    }

    override val toolbar by lazy { find<Toolbar>(R.id.toolbar) }

    private lateinit var description: TextView
    private lateinit var amount: TextView
    private lateinit var delay: TextView
    private lateinit var rate: TextView
    private lateinit var firstName: TextView
    private lateinit var lastName: TextView
    private lateinit var waveHeader: RelativeLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var userInformations: LinearLayout
    private lateinit var avatar: CircleImageView
    private lateinit var modifyLoan: Button
    private lateinit var scrollview: ScrollView

    private lateinit var loan: Loan
    private var otherUser: User? = null
    private var currentUser: User? = null
    private var color: LoansAdapter.Color? = LoansAdapter.Color.BLUE

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pending_loan_viewer)

        description = find(R.id.description)
        amount = find(R.id.amount)
        rate = find(R.id.rate)
        delay = find(R.id.delay)
        firstName = find(R.id.firstName)
        lastName = find(R.id.lastName)
        waveHeader = find(R.id.waveHeader)
        progressBar = find(R.id.progressBar)
        userInformations = find(R.id.userInformations)
        avatar = find(R.id.avatar)
        modifyLoan = find(R.id.modifyLoan)
        scrollview = find(R.id.scrollview)

        toolbarTitle = getString(R.string.loan_viewver_toolbar_title)
        enableHomeAsUp { onBackPressed() }

        scrollview.fullScroll(ScrollView.FOCUS_UP)

        if (intent == null)
        {
            finish()
        }


        if (intent.hasExtra(PublicLoansFragment.PUBLIC_LOAN_KEY))
        {
            loan = intent.getSerializableExtra(PublicLoansFragment.PUBLIC_LOAN_KEY) as Loan
            otherUser = intent.getStringExtra(USER_KEY) as User?
            accept.visibility = View.VISIBLE
            negociate.visibility = View.VISIBLE
            modifyLoan.visibility = View.GONE
        }
        else if (intent.hasExtra(MyLoansFragment.LOAN_KEY))
        {
            loan = intent.getSerializableExtra(MyLoansFragment.LOAN_KEY) as Loan
            otherUser = intent.getStringExtra(USER_KEY) as User?
            accept.visibility = View.GONE
            negociate.visibility = View.GONE
            modifyLoan.visibility = View.VISIBLE

        }

        if (otherUser == null)
        {
            findUserInformations()
        }
        else
        {
            removeLoaderOnNames()
        }

        color = intent.getSerializableExtra(InProgressLoanViewerActivity.COLOR_KEY) as LoansAdapter.Color?

        if (color == null)
        {
            color = LoansAdapter.Color.BLUE
        }

        changeColor(color!!, this)
        val drawable = if (color == LoansAdapter.Color.BLUE) R.drawable.vague else R.drawable.orange_wave

        waveHeader.background = ContextCompat.getDrawable(this, drawable)

        fillInformations()
        accept.setOnClickListener(this)
        negociate.setOnClickListener(this)
        modifyLoan.setOnClickListener(this)
        avatar.setOnClickListener(this)


        firstName.text = otherUser?.firstname
        lastName.text = otherUser?.lastname

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        if (requestCode == ChatDialogActivity.MODIFYING_REQUEST_CODE)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                this.loan = data?.getSerializableExtra(AddLoanActivity.RETURNED_LOAN_KEY) as Loan
                fillInformations()
            }
        }
    }

    override fun onClick(view: View?)
    {
        when (view?.id)
        {
            accept.id ->
            {
                accept()
            }
            negociate.id -> negociate()
            modifyLoan.id -> modify()
            avatar.id ->
            {
                val intent = Intent(PendingLoanViewerActivity@ this, ProfileViewerActivity::class.java)
                intent.putExtra(ProfileViewerActivity.USER_ID_KEY, loan.user_requester_id)
                val firstName = if (loan.user_requester_id == currentUser?.id) currentUser?.firstname else otherUser?.firstname
                intent.putExtra(ProfileViewerActivity.USER_FIRSTNAME_KEY, firstName)
                intent.putExtra(ProfileViewerActivity.COLOR_KEY, color)

                startActivity(intent)
            }
        }
    }

    private fun accept()
    {
        val intent = Intent(ctx, PaymentActivity::class.java)
        intent.putExtra(PaymentActivity.LOAN_KEY, loan)
        intent.putExtra(PaymentActivity.OTHER_USER_KEY, otherUser)
        startActivity(intent)
    }

    private fun negociate()
    {
        createAndLaunchNegociationChat()
    }

    private fun modify()
    {
        val intent = Intent(this@PendingLoanViewerActivity, AddLoanActivity::class.java)
        intent.putExtra(AddLoanActivity.IS_MODIFYYING_MODE_KEY, true)
        intent.putExtra(AddLoanActivity.MODIFYING_LOAN_KEY, loan)
        startActivityForResult(intent, ChatDialogActivity.MODIFYING_REQUEST_CODE)
    }

    private fun createAndLaunchNegociationChat()
    {
        val customAlert = customAlert(BeMyDialog.TYPE.LOADING, R.string.preparing_negociation)

        val currentUser = RealmServices.getCurrentUser(this)!!

        val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().time)

        val firstMessage = Message(getString(R.string.firstMessageInNegociation, currentUser.firstname), currentUser.id, time, currentUser?.firstname!!)
        val listMessages = ListMessages(getChannelIdentification(currentUser?.id, loan.user_requester_id).toInt(), mutableListOf(firstMessage))

        val listMessageReference = Utils.getDatabase().getReference("listMessages").child(listMessages.id.toString())

        val channel = Channel(loan.id, currentUser.id, loan.user_requester_id, listMessages.id)

        //listMessageReference.push().setValue(firstMessage)

        channel.last_message = firstMessage.text
        Utils.getDatabase().getReferenceFromUrl("https://bmybank-2146c.firebaseio.com/user-channels/${currentUser.id}/channels").child(getChannelIdentification(channel.id_user_1, channel.id_user_2)).setValue(channel)
        Utils.getDatabase().getReferenceFromUrl("https://bmybank-2146c.firebaseio.com/user-channels/${loan.user_requester_id}/channels").child(getChannelIdentification(channel.id_user_1, channel.id_user_2)).setValue(channel)
        listMessageReference.push().setValue(firstMessage)


        val intent = Intent(ctx, ChatDialogActivity::class.java)
        intent.putExtra(ChatDialogActivity.DISCUSSION_KEY, channel)
        startActivity(intent)

        finish()
    }

    private fun getChannelIdentification(userId1: Int, userId2: Int) = if (userId1 > userId2) "$userId1$userId2" else "$userId2$userId1"

    override fun onBackPressed()
    {
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun fillInformations()
    {
        description.text = loan.description
        amount.text = loan.amount.toString()
        rate.text = loan.rate.toString()
        delay.text = loan.delay.toString()
    }

    private fun findUserInformations()
    {
        val api = BmyBankApi.getInstance(ctx)
        val findUserByIdRequest = api.findUserById(loan.user_requester_id)


        findUserByIdRequest.enqueue(object : Callback<UserResponse>
        {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>)
            {
                val userResponse = response.body()
                if (userResponse?.success != null && userResponse?.success)
                {
                    otherUser = userResponse.user


                    firstName.text = otherUser?.firstname
                    lastName.text = otherUser?.lastname

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
        progressBar.visibility = View.GONE
        userInformations.visibility = View.VISIBLE

        var emailWithoutSpecialCharacters = otherUser?.email?.replace("@", "")
        emailWithoutSpecialCharacters = emailWithoutSpecialCharacters?.replace(".", "")
        emailWithoutSpecialCharacters = emailWithoutSpecialCharacters.plus(".jpg")


        if (emailWithoutSpecialCharacters != null)
        {
            val firebaseStorage = FirebaseStorage.getInstance()
            val reference = firebaseStorage.reference.child("${Constants.PROFILE_PICTURES_NODE}/$emailWithoutSpecialCharacters")

            // Load the image using Glide
            GlideApp.with(this)
                    .load(reference)
                    .placeholder(R.drawable.default_profile)
                    .error(R.drawable.default_profile)
                    .into(avatar)

        }

    }

}