package ecalle.com.bmybank

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import ecalle.com.bmybank.adapters.LoansAdapter
import ecalle.com.bmybank.adapters.RefundsAdapter
import ecalle.com.bmybank.firebase.GlideApp
import ecalle.com.bmybank.fragments.MyLoansFragment
import ecalle.com.bmybank.realm.RealmServices
import ecalle.com.bmybank.realm.bo.Loan
import ecalle.com.bmybank.realm.bo.Refund
import ecalle.com.bmybank.realm.bo.User
import ecalle.com.bmybank.services.BmyBankApi
import ecalle.com.bmybank.services_respnses_bo.RefundsResponse
import ecalle.com.bmybank.services_respnses_bo.UserResponse
import org.jetbrains.anko.ctx
import org.jetbrains.anko.find
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * Created by Thomas Ecalle on 10/04/2018.
 */
class FinishedLoanViewerActivity : AppCompatActivity(), ToolbarManager, View.OnClickListener
{


    companion object
    {
        val USER_KEY = "userKey"
    }

    override val toolbar by lazy { find<Toolbar>(R.id.toolbar) }

    private lateinit var amount: TextView
    private lateinit var delay: TextView
    private lateinit var rate: TextView
    private lateinit var firstName: TextView
    private lateinit var lastName: TextView
    private lateinit var waveHeader: RelativeLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var userInformations: LinearLayout
    private lateinit var avatar: CircleImageView
    private lateinit var refundsLayout: LinearLayout
    private lateinit var totalGainz: TextView
    private lateinit var gainzTitle: TextView

    private lateinit var refundsRecyclerView: RecyclerView
    private lateinit var refundsAdapter: RefundsAdapter
    private lateinit var refundsList: MutableList<Refund>
    private lateinit var refundsLoader: ProgressBar

    private lateinit var loan: Loan
    private var otherUser: User? = null
    private var currentUser: User? = null
    private var color: LoansAdapter.Color? = LoansAdapter.Color.BLUE

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finished_loan_viewer)

        amount = find(R.id.amount)
        rate = find(R.id.rate)
        delay = find(R.id.delay)
        firstName = find(R.id.firstName)
        lastName = find(R.id.lastName)
        waveHeader = find(R.id.waveHeader)
        progressBar = find(R.id.progressBar)
        userInformations = find(R.id.userInformations)
        avatar = find(R.id.avatar)
        refundsLayout = find(R.id.refundsLayout)
        refundsRecyclerView = find(R.id.recyclerView)
        refundsLoader = find(R.id.refundsLoader)
        totalGainz = find(R.id.totalGainz)
        gainzTitle = find(R.id.gainzTitle)

        toolbarTitle = getString(R.string.loan_viewver_toolbar_title)
        enableHomeAsUp { onBackPressed() }

        if (intent == null)
        {
            finish()
        }

        loan = intent.getSerializableExtra(MyLoansFragment.LOAN_KEY) as Loan
        otherUser = intent.getStringExtra(USER_KEY) as User?
        currentUser = RealmServices.getCurrentUser(this)
        getRefundsThenShow()

        if (otherUser == null)
        {
            findUserInformations()
        }
        else
        {
            removeLoaderOnNames()
        }

        color = LoansAdapter.Color.Grey

        changeColor(color!!, this)
        val drawable = R.drawable.grey_wave

        waveHeader.background = ContextCompat.getDrawable(this, drawable)

        fillInformations()
        firstName.text = otherUser?.firstname
        lastName.text = otherUser?.lastname

    }

    private fun getRefundsThenShow()
    {
        val api = BmyBankApi.getInstance(ctx)
        val findUserByIdRequest = api.getRefunds(loan.id)

        findUserByIdRequest.enqueue(object : Callback<RefundsResponse>
        {
            override fun onResponse(call: Call<RefundsResponse>, response: Response<RefundsResponse>)
            {
                val res = response.body()
                if (res?.success != null && res?.success)
                {
                    refundsList = res.refunds
                    refundsAdapter = RefundsAdapter(refundsList)

                    refundsRecyclerView.layoutManager = LinearLayoutManager(ctx)
                    refundsRecyclerView.adapter = refundsAdapter

                    refundsLoader.visibility = View.GONE
                    refundsRecyclerView.visibility = View.VISIBLE

                    var total = 0
                    for (refund in refundsList)
                    {
                        total += refund.amount
                    }

                    if (currentUser?.id == loan.user_requester_id)
                    {
                        gainzTitle.text = getString(R.string.gainz_interest)
                    }
                    else
                    {
                        gainzTitle.text = getString(R.string.gainz)
                    }

                    totalGainz.visibility = View.VISIBLE
                    totalGainz.text = (total - loan.amount).toString()

                }
            }


            override fun onFailure(call: Call<RefundsResponse>, t: Throwable)
            {
                toast(R.string.not_internet)
            }
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        if (requestCode == ChatDialogActivity.MODIFYING_REQUEST_CODE)
        {

        }
    }

    override fun onClick(view: View?)
    {
        when (view?.id)
        {

        }
    }

    override fun onBackPressed()
    {
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun fillInformations()
    {
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