package ecalle.com.bmybank

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import com.squareup.moshi.Moshi
import ecalle.com.bmybank.services_responses_bo.SImpleResponse
import ecalle.com.bmybank.services_responses_bo.UserResponse
import ecalle.com.bmybank.custom_components.BeMyDialog
import ecalle.com.bmybank.extensions.*
import ecalle.com.bmybank.realm.RealmServices
import ecalle.com.bmybank.realm.bo.User
import ecalle.com.bmybank.services.BmyBankApi
import org.jetbrains.anko.find
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Thomas Ecalle on 17/04/2018.
 */
class ProfileModificationActivity : AppCompatActivity(), View.OnClickListener
{

    lateinit private var scrollView: ScrollView
    lateinit private var email: EditText
    lateinit private var previousPassword: EditText
    lateinit private var password: EditText
    lateinit private var confirmPassword: EditText
    lateinit private var firstName: EditText
    lateinit private var lastName: EditText
    lateinit private var description: EditText
    lateinit private var errorView: TextView
    lateinit private var save: FloatingActionButton

    lateinit private var user: User
    private var loadingDialog: BeMyDialog? = null


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_modification)
        scrollView = find(R.id.scrollView)
        errorView = find(R.id.errorView)
        email = find(R.id.email)
        previousPassword = find(R.id.previousPassword)
        password = find(R.id.password)
        confirmPassword = find(R.id.confirmPassword)
        firstName = find(R.id.firstName)
        lastName = find(R.id.lastName)
        description = find(R.id.description)
        save = find(R.id.save)

        save.setOnClickListener(this)


        user = RealmServices.getCurrentUser(this) ?: return

        fillInformations()


        description.makeEditTextScrollableInScrollview()

    }


    private fun fillInformations()
    {
        firstName.setText(user.firstname)
        lastName.setText(user.lastname)
        email.setText(user.email)
        description.setText(user.description)
    }


    override fun onClick(view: View?)
    {
        when (view?.id)
        {
            R.id.save -> saveUser()
        }
    }

    private fun saveUser()
    {
        when
        {
            isNotValidEmailAdress() -> showInformation(getString(R.string.not_valid_email_adress))
            passwordAreDifferent() -> showInformation(getString(R.string.passwords_not_equals))
            namesAreNotWellFormat() -> showInformation(getString(R.string.not_well_format_names))
            descriptionIsNotWellFormat() -> showInformation(getString(R.string.not_well_format_description))
            else ->
            {
                var needToUpdatePassword = false
                var needToUpdateEmail = false
                var previousPasswordValue = ""

                if (email.textValue != user.email)
                {
                    needToUpdateEmail = true
                    user.email = email.textValue
                }

                if (!password.textValue.isEmpty())
                {
                    needToUpdatePassword = true
                    previousPasswordValue = previousPassword.textValue
                }

                user.lastname = lastName.textValue
                user.firstname = firstName.textValue
                user.description = description.textValue

                loadingDialog = customAlert(message = R.string.user_updating_loading, type = BeMyDialog.TYPE.LOADING)


                val api = BmyBankApi.getInstance(this)

                val updateRequest =
                        api.updateUser(id = user.id,
                                email = if (needToUpdateEmail) user.email else null,
                                previousPassword = if (needToUpdatePassword) previousPasswordValue else null,
                                newPassword = if (needToUpdatePassword) password.textValue else null,
                                lastname = user.lastname,
                                firstname = user.firstname,
                                description = user.description)

                updateRequest.enqueue(object : Callback<UserResponse>
                {
                    override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>)
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
                                    showInformation(getString(R.string.impossible_user_update))
                                }
                                loadingDialog?.dismiss()
                            }
                            else ->
                            {
                                showInformation(information = getString(R.string.user_update_success), error = false)

                                RealmServices.saveCurrentuser(user)

                                loadingDialog?.dismiss()
                            }
                        }

                        fillInformations()

                    }

                    override fun onFailure(call: Call<UserResponse>, t: Throwable)
                    {
                        //toast("Failure getting user from server, throwable message : ${t.message}")
                        loadingDialog?.dismiss()
                        showInformation(getString(R.string.not_internet))
                    }
                })

            }
        }
    }

    private fun showInformation(information: String = "", show: Boolean = true, error: Boolean = true)
    {
        errorView.text = information
        val color = if (error) ContextCompat.getColor(this, R.color.red) else ContextCompat.getColor(this, R.color.green)
        errorView.setTextColor(color)
        errorView.visibility = if (show) View.VISIBLE else View.GONE
        scrollView.fullScroll(ScrollView.FOCUS_UP)
    }

    private fun passwordAreDifferent(): Boolean
    {
        return (password.textValue != confirmPassword.textValue)
                || (!previousPassword.textValue.isEmpty() && password.isEmpty())
                || (previousPassword.textValue.isEmpty() && !password.isEmpty())
    }

    private fun isNotValidEmailAdress(): Boolean
    {
        return !android.util.Patterns.EMAIL_ADDRESS.matcher(email.textValue).matches();
    }

    private fun namesAreNotWellFormat(): Boolean
    {
        return !firstName.hasOnlyLetters() || !lastName.hasOnlyLetters()
    }

    private fun descriptionIsNotWellFormat(): Boolean
    {
        return description.textValue.length >= Constants.DESCRIPTION_MAX_LENGTH
    }


}