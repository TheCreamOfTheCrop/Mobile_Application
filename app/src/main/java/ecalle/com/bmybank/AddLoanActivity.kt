package ecalle.com.bmybank

import android.app.Activity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import com.squareup.moshi.Moshi
import ecalle.com.bmybank.bo.AddingLoanResponse
import ecalle.com.bmybank.bo.SImpleResponse
import ecalle.com.bmybank.custom_components.BeMyDialog
import ecalle.com.bmybank.extensions.customAlert
import ecalle.com.bmybank.extensions.log
import ecalle.com.bmybank.extensions.makeEditTextScrollableInScrollview
import ecalle.com.bmybank.extensions.textValue
import ecalle.com.bmybank.realm.RealmServices
import ecalle.com.bmybank.services.BmyBankApi
import org.jetbrains.anko.alert
import org.jetbrains.anko.find
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Thomas Ecalle on 02/04/2018.
 */
class AddLoanActivity : AppCompatActivity(), ToolbarManager, View.OnClickListener
{

    override val toolbar by lazy { find<Toolbar>(R.id.toolbar) }

    private lateinit var description: EditText
    private lateinit var amount: EditText
    private lateinit var repayment: EditText
    private lateinit var rate: EditText
    private lateinit var validate: Button
    private lateinit var errorView: TextView
    private lateinit var scrollView: ScrollView
    private lateinit var loadingDialog: BeMyDialog

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_loan)

        toolbarTitle = getString(R.string.add_loan_title)
        enableHomeAsUp { onBackPressed() }

        description = find(R.id.description)
        amount = find(R.id.amount)
        repayment = find(R.id.repayment)
        rate = find(R.id.rate)
        errorView = find(R.id.errorView)
        scrollView = find(R.id.scrollView)
        validate = find(R.id.validate)

        description.makeEditTextScrollableInScrollview()
        validate.setOnClickListener(this)
    }

    override fun onClick(view: View?)
    {
        when (view?.id)
        {
            validate.id -> checkThenSend()
        }
    }

    override fun onBackPressed()
    {
        log("on back pressed")
        confirmStoppingInscription()
    }

    private fun confirmStoppingInscription()
    {
        alert {
            message = getString(R.string.quitting_loan_creation_message)
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
            else -> addLoan()
        }
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
                        loadingDialog.dismiss()
                    }
                    else ->
                    {
                        loadingDialog.dismiss()
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