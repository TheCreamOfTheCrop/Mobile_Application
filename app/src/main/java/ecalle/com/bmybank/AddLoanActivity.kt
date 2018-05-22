package ecalle.com.bmybank

import android.app.Activity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.*
import com.squareup.moshi.Moshi
import de.hdodenhof.circleimageview.CircleImageView
import ecalle.com.bmybank.bo.AddingLoanResponse
import ecalle.com.bmybank.bo.SImpleResponse
import ecalle.com.bmybank.custom_components.BeMyDialog
import ecalle.com.bmybank.extensions.*
import ecalle.com.bmybank.realm.RealmServices
import ecalle.com.bmybank.realm.bo.Loan
import ecalle.com.bmybank.realm.bo.User
import ecalle.com.bmybank.services.BmyBankApi
import org.jetbrains.anko.alert
import org.jetbrains.anko.find
import org.jetbrains.anko.toast
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
    }

    private lateinit var description: EditText
    private lateinit var amount: EditText
    private lateinit var repayment: EditText
    private lateinit var rate: EditText
    private lateinit var validate: Button
    private lateinit var errorView: TextView
    private lateinit var publicButton: RadioButton
    private lateinit var privateButton: RadioButton
    private lateinit var scrollView: ScrollView
    private lateinit var loadingDialog: BeMyDialog

    private lateinit var otherUserLayout: LinearLayout
    private lateinit var otherUserImage: CircleImageView
    private lateinit var otherUserLastName: TextView
    private lateinit var otherUserFirstName: TextView
    private var isPublicType = true
    private lateinit var otherUser: User


    private var isModifyingMode: Boolean = false
    private lateinit var negociatedLoan: Loan

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_loan)

        changeStatusBar(R.color.colorPrimary, this)


        isModifyingMode = intent.getBooleanExtra(IS_MODIFYYING_MODE_KEY, false)

        if (isModifyingMode && intent.getSerializableExtra(MODIFYING_LOAN_KEY) != null)
        {
            negociatedLoan = intent.getSerializableExtra(MODIFYING_LOAN_KEY) as Loan
            otherUser = intent.getSerializableExtra(MODIFYING_LOAN_OTHER_USER_KEY) as User
        }

        description = find(R.id.description)
        amount = find(R.id.amount)
        repayment = find(R.id.repayment)
        rate = find(R.id.rate)
        errorView = find(R.id.errorView)
        scrollView = find(R.id.scrollView)
        publicButton = find(R.id.publicButton)
        privateButton = find(R.id.privateButton)
        validate = find(R.id.validate)
        otherUserLayout = find(R.id.otherUserLayout)
        otherUserImage = find(R.id.otherUserImage)
        otherUserLastName = find(R.id.otherUserLastName)
        otherUserFirstName = find(R.id.otherUserFirstName)

        publicButton.isChecked = true



        if (isModifyingMode)
        {
            publicButton.isEnabled = false
            privateButton.isEnabled = false
            privateButton.isChecked = true

            description.isEnabled = false

            amount.setText(negociatedLoan.amount.toString())
            description.setText(negociatedLoan.description)
            rate.setText(negociatedLoan.rate.toString())
            repayment.setText(negociatedLoan.delay.toString())
            validate.text = getString(R.string.modifyAndShareMyLoan)


            //otherUserImage
            otherUserLastName.text = otherUser.lastname
            otherUserFirstName.text = otherUser.firstname

            otherUserLayout.visibility = View.VISIBLE

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
        }
    }

    private fun toggleButtons(isPublic: Boolean = true)
    {
        isPublicType = isPublic
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
            else -> if (!isModifyingMode) addLoan() else negociate()
        }
    }

    private fun negociate()
    {
        toast("${otherUser.lastname}/${otherUser.firstname}/ is public = ${publicButton.isChecked}")
        /*

        loadingDialog = customAlert(message = R.string.negociating_loading, type = BeMyDialog.TYPE.LOADING)
        val delay = if (repayment.textValue.isEmpty()) repayment.hint.toString().toInt() else repayment.textValue.toInt()

        val api = BmyBankApi.getInstance(this)
        val request = api.addNegociation(negociatedLoan.id, amount.textValue.toFloatOrNull(), rate.textValue.toFloatOrNull(), delay)


        request.enqueue(object : Callback<AddingNegociationResponse>
        {
            override fun onResponse(call: Call<AddingNegociationResponse>, response: Response<AddingNegociationResponse>)
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
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                    else
                    {
                        showInformation(getString(R.string.server_issue))
                    }
                }
            }

            override fun onFailure(call: Call<AddingNegociationResponse>, t: Throwable)
            {
                //toast("Failure getting user from server, throwable message : ${t.message}")
                showInformation(getString(R.string.not_internet))

            }
        })

        */

    }

    private fun addLoan()
    {
        val repaymentValueInMonths = if (repayment.textValue.isEmpty()) repayment.hint.toString().toInt() else repayment.textValue.toInt()

        val currentUser = RealmServices.getCurrentUser(this)
        val api = BmyBankApi.getInstance(this)
        val addLoanRequest = api.addLoan(amount = amount.textValue.toFloatOrNull(),
                description = if (description.textValue !== null) description.textValue else "",
                rate = rate.textValue.toFloatOrNull(),
                userId = currentUser?.id,
                delay = repaymentValueInMonths)

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
        errorView.text = information
        val color = if (error) ContextCompat.getColor(this, R.color.red) else ContextCompat.getColor(this, R.color.green)
        errorView.setTextColor(color)
        errorView.visibility = if (show) View.VISIBLE else View.GONE
        scrollView.fullScroll(ScrollView.FOCUS_UP)
    }

    private fun amountNotWellSet(): Boolean
    {
        return amount.textValue.isEmpty() || (amount.textValue.toFloatOrNull() === null)
    }

    private fun rateNotWellSet(): Boolean
    {
        return rate.textValue.isEmpty() || (rate.textValue.toFloatOrNull() === null)
    }

    private fun descriptionIsNotWellFormat(): Boolean
    {
        return description.textValue.length >= Constants.DESCRIPTION_MAX_LENGTH
    }

    private fun repaymentIsNotValid(): Boolean
    {
        val value = if (repayment.textValue.isEmpty()) repayment.hint.toString().toInt() else repayment.textValue.toInt()
        return value < 0
    }
}