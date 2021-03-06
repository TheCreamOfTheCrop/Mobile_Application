package ecalle.com.bmybank

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.*
import com.squareup.moshi.Moshi
import de.hdodenhof.circleimageview.CircleImageView
import ecalle.com.bmybank.custom_components.BeMyDialog
import ecalle.com.bmybank.extensions.*
import ecalle.com.bmybank.realm.RealmServices
import ecalle.com.bmybank.realm.bo.Loan
import ecalle.com.bmybank.realm.bo.User
import ecalle.com.bmybank.services.BmyBankApi
import ecalle.com.bmybank.services_responses_bo.AddingLoanResponse
import ecalle.com.bmybank.services_responses_bo.SImpleResponse
import org.jetbrains.anko.alert
import org.jetbrains.anko.find
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * Created by Thomas Ecalle on 02/04/2018.
 */
class AddLoanActivity : AppCompatActivity(), View.OnClickListener
{
    companion object
    {
        val IS_MODIFYYING_MODE_KEY = "isModifyingModeKey"
        val MODIFYING_LOAN_KEY = "modifyingLoanKey"
        val MODIFYING_LOAN_OTHER_USER_KEY = "modifyingLoanOtherUserKey"
        val RETURNED_LOAN_KEY = "returnedLoanKey"
    }

    private lateinit var description: EditText
    private lateinit var amount: EditText
    private lateinit var repayment: EditText
    private lateinit var rate: EditText
    private lateinit var validate: Button
    private lateinit var publicButton: TextView
    private lateinit var privateButton: TextView
    private lateinit var scrollView: ScrollView
    private lateinit var loadingDialog: BeMyDialog
    private lateinit var exit: FloatingActionButton
    private lateinit var otherUserLayout: LinearLayout
    private lateinit var otherUserImage: CircleImageView
    private lateinit var otherUserLastName: TextView
    private lateinit var otherUserFirstName: TextView
    private var isPublicType = true
    private lateinit var otherUser: User
    private lateinit var currrentUser: User


    private var isModifyingMode: Boolean = false
    private var isModifyingModeToSend: Boolean = false
    private lateinit var negociatedLoan: Loan

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_loan)

        changeStatusBar(R.color.white, this)

        isModifyingMode = intent.getBooleanExtra(IS_MODIFYYING_MODE_KEY, false)

        if (isModifyingMode && intent.getSerializableExtra(MODIFYING_LOAN_KEY) != null)
        {
            negociatedLoan = intent.getSerializableExtra(MODIFYING_LOAN_KEY) as Loan

            if (intent.getSerializableExtra(MODIFYING_LOAN_OTHER_USER_KEY) != null)
            {
                isModifyingModeToSend = true
                otherUser = intent.getSerializableExtra(MODIFYING_LOAN_OTHER_USER_KEY) as User
            }
        }

        currrentUser = RealmServices.getCurrentUser(this) as User

        description = find(R.id.description)
        amount = find(R.id.amount)
        repayment = find(R.id.repayment)
        rate = find(R.id.rate)
        scrollView = find(R.id.scrollView)
        publicButton = find(R.id.publicButton)
        privateButton = find(R.id.privateButton)
        validate = find(R.id.validate)
        otherUserLayout = find(R.id.otherUserLayout)
        otherUserImage = find(R.id.otherUserImage)
        otherUserLastName = find(R.id.otherUserLastName)
        otherUserFirstName = find(R.id.otherUserFirstName)
        exit = find(R.id.exit)

        publicButton.setOnClickListener(this)
        privateButton.setOnClickListener(this)
        exit.setOnClickListener(this)

        publicButton.callOnClick()
        publicButton.isEnabled = false
        privateButton.isEnabled = false

        if (isModifyingMode)
        {
            validate.text = getString(R.string.modifyMyLoan)
            publicButton.isEnabled = false
            privateButton.isEnabled = false

            amount.setText(negociatedLoan.amount.toString())
            description.setText(negociatedLoan.description)
            rate.setText(negociatedLoan.rate.toString())
            repayment.setText(negociatedLoan.delay.toString())

            if (isModifyingModeToSend)
            {
                privateButton.callOnClick()
                description.isEnabled = false

                validate.text = getString(R.string.modifyAndShareMyLoan)

                //otherUserImage
                otherUserLastName.text = otherUser.lastname
                otherUserFirstName.text = otherUser.firstname

                otherUserLayout.visibility = View.VISIBLE
            }

        }

        description.makeEditTextScrollableInScrollview()
        validate.setOnClickListener(this)
    }

    override fun onClick(view: View?)
    {
        when (view?.id)
        {
            validate.id -> checkThenSend()
            publicButton.id -> toggleButtons()
            privateButton.id -> toggleButtons(false)
            exit.id -> onBackPressed()
        }
    }

    private fun toggleButtons(isPublic: Boolean = true)
    {
        isPublicType = isPublic
        if (isPublic)
        {
            privateButton.isSelected = false
            privateButton.background = ContextCompat.getDrawable(this, R.drawable.custom_blue_choice_button_background)
            privateButton.textColor = ContextCompat.getColor(this, R.color.colorPrimary)

            publicButton.isSelected = true
            publicButton.background = ContextCompat.getDrawable(this, R.drawable.custom_blue_choice_button_background_pressed)
            publicButton.textColor = ContextCompat.getColor(this, R.color.white)
        }
        else
        {
            publicButton.isSelected = false
            publicButton.background = ContextCompat.getDrawable(this, R.drawable.custom_blue_choice_button_background)
            publicButton.textColor = ContextCompat.getColor(this, R.color.colorPrimary)

            privateButton.isSelected = true
            privateButton.background = ContextCompat.getDrawable(this, R.drawable.custom_blue_choice_button_background_pressed)
            privateButton.textColor = ContextCompat.getColor(this, R.color.white)
        }
    }

    override fun onBackPressed()
    {
        log("on back pressed")
        confirmStoppingLoanEdition()
    }

    private fun confirmStoppingLoanEdition()
    {
        alert {
            message = if (!isModifyingMode) getString(R.string.quitting_loan_creation_message) else getString(R.string.quittin_loan_modification)
            positiveButton(R.string.yes) {
                super.onBackPressed()
                finish()
            }

            negativeButton(R.string.no) {}
        }.show()
    }

    private fun checkThenSend()
    {
        when
        {
            descriptionIsNotWellFormat() -> showInformation(getString(R.string.not_well_format_description))
            amountNotWellSet() -> showInformation(getString(R.string.incorrect_amount))
            rateNotWellSet() -> showInformation(getString(R.string.incorrect_rate))
            repaymentIsNotValid() -> showInformation(getString(R.string.incorrect_repayment))
            else ->
            {
                if (!isModifyingMode)
                {
                    addLoan()
                }
                else if (isModifyingModeToSend)
                {
                    sendPrivate()
                }
                else
                {
                    modify()
                }
            }
        }
    }

    private fun repaymentInMonths(): Int
    {
        return if (repayment.textValue.isEmpty()) repayment.hint.toString().toInt() else repayment.textValue.toInt()
    }

    private fun modify()
    {

        val api = BmyBankApi.getInstance(this)

        val updateLoanRequest = api.updateLoan(
                amount = amount.textValue.toFloatOrNull(),
                description = if (description.textValue !== null) description.textValue else "",
                rate = rate.textValue.toFloatOrNull(),
                idLoan = negociatedLoan.id,
                delay = repaymentInMonths())

        loadingDialog = customAlert(message = R.string.add_loan_loading, type = BeMyDialog.TYPE.LOADING)


        updateLoanRequest.enqueue(object : Callback<SImpleResponse>
        {
            override fun onResponse(call: Call<SImpleResponse>, response: Response<SImpleResponse>)
            {
                loadingDialog.dismiss()

                when
                {
                    response.code() == 404 -> showInformation(getString(R.string.server_issue))
                    response.code() == 400 ->
                    {
                        if (response.errorBody() != null)
                        {
                            val stringResponse = response.errorBody()!!.string()
                            val moshi = Moshi.Builder().build()
                            val jsonAdapter = moshi.adapter(SImpleResponse::class.java)
                            val response = jsonAdapter.fromJson(stringResponse)

                            showInformation(response.message)

                        }
                        else
                        {
                            showInformation(getString(R.string.impossible_loan_add))
                        }
                    }
                    else ->
                    {
                        val loan = Loan()
                        loan.amount = if (amount.textValue.toFloatOrNull() != null) amount.textValue.toFloat() else negociatedLoan.amount
                        loan.description = if (description.textValue !== null) description.textValue else ""
                        loan.rate = if (rate.textValue.toFloatOrNull() != null) rate.textValue.toFloat() else negociatedLoan.rate
                        loan.id = negociatedLoan.id
                        loan.delay = repaymentInMonths()


                        val resultIntent = Intent()
                        resultIntent.putExtra(RETURNED_LOAN_KEY, loan)
                        setResult(Activity.RESULT_OK, resultIntent)
                        finish()
                    }
                }


            }

            override fun onFailure(call: Call<SImpleResponse>, t: Throwable)
            {
                //toast("Failure getting user from server, throwable message : ${t.message}")
                loadingDialog?.dismiss()
                showInformation(getString(R.string.not_internet))
            }
        })
    }

    private fun sendPrivate()
    {
        loadingDialog = customAlert(message = R.string.negociating_loading, type = BeMyDialog.TYPE.LOADING)
        val delay = if (repayment.textValue.isEmpty()) repayment.hint.toString().toInt() else repayment.textValue.toInt()

        val api = BmyBankApi.getInstance(this)
        val request = api.updateLoan(
                idLoan = negociatedLoan.id,
                amount = amount.textValue.toFloatOrNull(),
                rate = rate.textValue.toFloatOrNull(),
                delay = delay,
                description = if (description.textValue !== null) description.textValue else "",
                loanType = "prive",
                providerId = otherUser?.id
        )


        request.enqueue(object : Callback<SImpleResponse>
        {
            override fun onResponse(call: Call<SImpleResponse>, response: Response<SImpleResponse>)
            {

                loadingDialog.dismiss()

                if (response.code() == 400)
                {
                    showInformation(getString(R.string.server_issue))
                }
                else
                {
                    val acceptResponse = response.body()
                    if (acceptResponse?.success!!)
                    {

                        val loan = Loan()
                        loan.amount = if (amount.textValue.toFloatOrNull() != null) amount.textValue.toFloat() else negociatedLoan.amount
                        loan.description = if (description.textValue !== null) description.textValue else ""
                        loan.rate = if (rate.textValue.toFloatOrNull() != null) rate.textValue.toFloat() else negociatedLoan.rate
                        loan.id = negociatedLoan.id
                        loan.delay = repaymentInMonths()
                        loan.loan_type = "prive"
                        loan.user_provider_id = otherUser.id
                        loan.user_requester_id = currrentUser.id
                        loan.state_id = Constants.STATE_WAITING


                        val resultIntent = Intent()
                        resultIntent.putExtra(RETURNED_LOAN_KEY, loan)
                        setResult(Activity.RESULT_OK, resultIntent)
                        finish()

                    }
                    else
                    {
                        showInformation(getString(R.string.server_issue))
                    }
                }
            }

            override fun onFailure(call: Call<SImpleResponse>, t: Throwable)
            {
                //toast("Failure getting user from server, throwable message : ${t.message}")
                showInformation(getString(R.string.not_internet))

            }
        })


    }

    private fun addLoan()
    {

        val currentUser = RealmServices.getCurrentUser(this)
        val api = BmyBankApi.getInstance(this)
        val addLoanRequest = api.addLoan(amount = amount.textValue.toFloatOrNull(),
                description = if (description.textValue !== null) description.textValue else "",
                rate = rate.textValue.toFloatOrNull(),
                userId = currentUser?.id,
                delay = repaymentInMonths())

        loadingDialog = customAlert(message = R.string.add_loan_loading, type = BeMyDialog.TYPE.LOADING)


        addLoanRequest.enqueue(object : Callback<AddingLoanResponse>
        {
            override fun onResponse(call: Call<AddingLoanResponse>, response: Response<AddingLoanResponse>)
            {
                loadingDialog.dismiss()

                when
                {
                    response.code() == 404 -> showInformation(getString(R.string.server_issue))
                    response.code() == 400 ->
                    {
                        if (response.errorBody() != null)
                        {
                            val stringResponse = response.errorBody()!!.string()
                            val moshi = Moshi.Builder().build()
                            val jsonAdapter = moshi.adapter(SImpleResponse::class.java)
                            val response = jsonAdapter.fromJson(stringResponse)

                            showInformation(response.message)

                        }
                        else
                        {
                            showInformation(getString(R.string.impossible_loan_add))
                        }
                    }
                    else ->
                    {
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                }


            }

            override fun onFailure(call: Call<AddingLoanResponse>, t: Throwable)
            {
                //toast("Failure getting user from server, throwable message : ${t.message}")
                loadingDialog?.dismiss()
                showInformation(getString(R.string.not_internet))
            }
        })


    }

    private fun showInformation(information: String = "", show: Boolean = true, error: Boolean = true)
    {
        if (error)
        {
            alertError(information)
        }
        scrollView.fullScroll(ScrollView.FOCUS_UP)
    }

    private fun amountNotWellSet(): Boolean
    {
        return amount.textValue.isNullOrEmpty() || (amount.textValue.toFloatOrNull() === null) || amount.textValue.toFloat() > 1000
    }

    private fun rateNotWellSet(): Boolean
    {
        return rate.textValue.isNullOrEmpty() || (rate.textValue.toFloatOrNull() === null)
    }

    private fun descriptionIsNotWellFormat(): Boolean
    {
        if (description.textValue.isNullOrEmpty()) return false

        return description.textValue.length >= Constants.DESCRIPTION_MAX_LENGTH
    }

    private fun repaymentIsNotValid(): Boolean
    {
        if (repayment.textValue.isNullOrEmpty() && repayment.hint == null)
        {
            return true
        }

        val value = if (repayment.textValue.isNullOrEmpty()) repayment.hint.toString().toInt() else repayment.textValue.toIntOrNull()
        return value == null || value.toInt() < 0
    }
}