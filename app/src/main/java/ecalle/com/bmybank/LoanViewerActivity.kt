package ecalle.com.bmybank

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.*
import ecalle.com.bmybank.adapters.LoansAdapter
import ecalle.com.bmybank.bo.SImpleResponse
import ecalle.com.bmybank.bo.UserResponse
import ecalle.com.bmybank.custom_components.BeMyDialog
import ecalle.com.bmybank.extensions.customAlert
import ecalle.com.bmybank.fragments.MyLoansFragment
import ecalle.com.bmybank.fragments.PublicLoansFragment
import ecalle.com.bmybank.realm.bo.Loan
import ecalle.com.bmybank.services.BmyBankApi
import org.jetbrains.anko.alert
import org.jetbrains.anko.ctx
import org.jetbrains.anko.find
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Thomas Ecalle on 10/04/2018.
 */
class LoanViewerActivity : AppCompatActivity(), ToolbarManager, View.OnClickListener
{


    companion object
    {
        val USER_FIRSTNAME_KEY = "userFirstNameKey"
        val USER_LASTNAME_KEY = "userLastNameKey"
        val COLOR_KEY = "colorKey"
        val REQUEST_CODE = 22
    }

    override val toolbar by lazy { find<Toolbar>(R.id.toolbar) }

    private lateinit var description: TextView
    private lateinit var amount: TextView
    private lateinit var delay: TextView
    private lateinit var rate: TextView
    private lateinit var firstName: TextView
    private lateinit var lastName: TextView
    private lateinit var accept: Button
    private lateinit var negociate: Button
    private lateinit var waveHeader: RelativeLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var userInformations: LinearLayout

    private lateinit var loan: Loan
    private var userFirstName: String? = null
    private var userLastName: String? = null
    private var color: LoansAdapter.Color? = LoansAdapter.Color.BLUE

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loan_viewer)

        description = find(R.id.description)
        amount = find(R.id.amount)
        rate = find(R.id.rate)
        delay = find(R.id.delay)
        firstName = find(R.id.firstName)
        lastName = find(R.id.lastName)
        accept = find(R.id.accept)
        negociate = find(R.id.negociate)
        waveHeader = find(R.id.waveHeader)
        progressBar = find(R.id.progressBar)
        userInformations = find(R.id.userInformations)

        toolbarTitle = getString(R.string.loan_viewver_toolbar_title)
        enableHomeAsUp { onBackPressed() }

        if (intent == null)
        {
            finish()
        }

        if (intent.hasExtra(PublicLoansFragment.PUBLIC_LOAN_KEY))
        {
            loan = intent.getSerializableExtra(PublicLoansFragment.PUBLIC_LOAN_KEY) as Loan
            userFirstName = intent.getStringExtra(USER_FIRSTNAME_KEY)
            userLastName = intent.getStringExtra(USER_LASTNAME_KEY)
            accept.visibility = View.VISIBLE
            negociate.visibility = View.VISIBLE
        }
        else if (intent.hasExtra(MyLoansFragment.MY_LOAN_KEY))
        {
            loan = intent.getSerializableExtra(MyLoansFragment.MY_LOAN_KEY) as Loan
            userFirstName = intent.getStringExtra(USER_FIRSTNAME_KEY)
            userLastName = intent.getStringExtra(USER_LASTNAME_KEY)
            accept.visibility = View.GONE
            negociate.visibility = View.GONE
        }

        if (userFirstName.isNullOrEmpty() || userLastName.isNullOrEmpty())
        {
            findUserInformations()
        }
        else
        {
            removeLoaderOnNames()
        }

        color = intent.getSerializableExtra(LoanViewerActivity.COLOR_KEY) as LoansAdapter.Color?

        if (color == null)
        {
            color = LoansAdapter.Color.BLUE
        }

        changeColor(color!!, this)
        val drawable = if (color == LoansAdapter.Color.BLUE) R.drawable.vague else R.drawable.orange_wave

        waveHeader.background = ContextCompat.getDrawable(this, drawable)

        description.text = loan.description
        amount.text = loan.amount.toString()
        rate.text = loan.rate.toString()
        delay.text = loan.delay.toString()
        accept.setOnClickListener(this)
        negociate.setOnClickListener(this)
        firstName.text = userFirstName
        lastName.text = userLastName

    }

    override fun onClick(view: View?)
    {
        when (view?.id)
        {
            accept.id ->
            {
                alert {
                    message = getString(R.string.accepting_loan_confirmation)
                    positiveButton(R.string.yes) {
                        accept()
                    }

                    negativeButton(R.string.no) {}
                }.show()
            }
            negociate.id -> negociate()
        }
    }

    private fun accept()
    {
        val customAlert = customAlert(BeMyDialog.TYPE.LOADING, R.string.acceptin_loan)

        val api = BmyBankApi.getInstance(this)
        val request = api.acceptDirectLoan(loan.id)

        request.enqueue(object : Callback<SImpleResponse>
        {
            override fun onResponse(call: Call<SImpleResponse>, response: Response<SImpleResponse>)
            {
                customAlert.dismiss()

                if (response.code() == 400)
                {
                    toast("error 400")
                }
                else
                {
                    val acceptResponse = response.body()
                    if (acceptResponse?.success!!)
                    {
                        toast("Prêt accepté !")
                    }
                    else
                    {
                        toast(acceptResponse.message)
                    }
                }
            }

            override fun onFailure(call: Call<SImpleResponse>, t: Throwable)
            {
                //toast("Failure getting user from server, throwable message : ${t.message}")
                customAlert.dismiss()

                toast(R.string.not_internet)
            }
        })
    }

    private fun negociate()
    {
        val intent = Intent(this, AddLoanActivity::class.java)
        intent.putExtra(AddLoanActivity.IS_NEGOCIATING_MODE_KEY, true)
        intent.putExtra(AddLoanActivity.NEGOCIATED_LOAN_KEY, loan)
        startActivityForResult(intent, REQUEST_CODE)
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
                    val user = userResponse.user
                    userFirstName = user.firstname
                    userLastName = user.lastname

                    firstName.text = userFirstName
                    lastName.text = userLastName

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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        if (requestCode == LoanViewerActivity.REQUEST_CODE)
        {
            when (resultCode)
            {
                Activity.RESULT_OK -> customAlert(message = R.string.negociation_well_sent, loop = false)
            }
        }
    }


}