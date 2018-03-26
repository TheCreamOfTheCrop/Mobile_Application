package ecalle.com.bmybank.fragments.inscription_steps

import android.app.Fragment
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import ecalle.com.bmybank.Constants
import ecalle.com.bmybank.R
import ecalle.com.bmybank.bo.LoginAndRegisterResponse
import ecalle.com.bmybank.custom_components.BeMyDialog
import ecalle.com.bmybank.extensions.customAlert
import ecalle.com.bmybank.extensions.hasOnlyLetters
import ecalle.com.bmybank.extensions.isEmpty
import ecalle.com.bmybank.extensions.textValue
import ecalle.com.bmybank.realm.RealmServices
import ecalle.com.bmybank.realm.bo.User
import ecalle.com.bmybank.services.BmyBankApi
import org.jetbrains.anko.find
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Thomas Ecalle on 18/03/2018.
 */
class ProfileModificationFragment : Fragment(), View.OnClickListener
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


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_profile_modification, container, false)

        scrollView = view.find(R.id.scrollView)
        errorView = view.find(R.id.errorView)
        email = view.find(R.id.email)
        previousPassword = view.find(R.id.previousPassword)
        password = view.find(R.id.password)
        confirmPassword = view.find(R.id.confirmPassword)
        firstName = view.find(R.id.firstName)
        lastName = view.find(R.id.lastName)
        description = view.find(R.id.description)
        save = view.find(R.id.save)

        save.setOnClickListener(this)

        if (arguments != null)
        {
            user = arguments.getSerializable(Constants.SERIALIZED_OBJECT_KEY) as User

            fillInformations()

        }


        makeDescriptionScrollable()

        return view
    }

    private fun fillInformations()
    {
        firstName.setText(user.firstname)
        lastName.setText(user.lastname)
        email.setText(user.email)
        description.setText(user.description)
    }

    private fun makeDescriptionScrollable()
    {
        description.isVerticalScrollBarEnabled = true
        description.overScrollMode = View.OVER_SCROLL_ALWAYS
        description.scrollBarStyle = View.SCROLLBARS_INSIDE_INSET
        description.movementMethod = ScrollingMovementMethod.getInstance()

        description.setOnTouchListener(
                { view, motionEvent ->
                    view?.parent?.requestDisallowInterceptTouchEvent(true)
                    if (motionEvent.action and MotionEvent.ACTION_UP != 0 && motionEvent.actionMasked and MotionEvent.ACTION_UP != 0)
                    {
                        view?.parent?.requestDisallowInterceptTouchEvent(false)
                    }
                    false
                })
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
            isNotValidEmailAdress() -> showError(getString(R.string.not_valid_email_adress))
            passwordAreDifferent() -> showError(getString(R.string.passwords_not_equals))
            namesAreNotWellFormat() -> showError(getString(R.string.not_well_format_names))
            descriptionIsNotWellFormat() -> showError(getString(R.string.not_well_format_description))
            else ->
            {
                user.email = email.textValue
                if (!password.textValue.isEmpty())
                {
                    user.password = password.textValue
                }

                user.lastname = lastName.textValue
                user.firstname = firstName.textValue
                user.description = description.textValue

                loadingDialog = customAlert(message = R.string.user_updating_loading, type = BeMyDialog.TYPE.LOADING)


                val api = BmyBankApi.getInstance()
                val updateRequest = api.updateUser(user.id, user.email, user.password, user.lastname, user.firstname, user.description)

                updateRequest.enqueue(object : Callback<LoginAndRegisterResponse>
                {
                    override fun onResponse(call: Call<LoginAndRegisterResponse>, andRegisterResponse: Response<LoginAndRegisterResponse>)
                    {
                        if (andRegisterResponse.code() == 400)
                        {
                            showError(getString(R.string.bad_password))
                            loadingDialog?.dismiss()
                        }
                        else
                        {
                            showError(show = false)

                            fillInformations()
                            RealmServices.saveCurrentuser(user)

                            loadingDialog?.dismiss()

                        }


                    }

                    override fun onFailure(call: Call<LoginAndRegisterResponse>, t: Throwable)
                    {
                        //toast("Failure getting user from server, throwable message : ${t.message}")
                        loadingDialog?.dismiss()
                        showError(getString(R.string.not_internet))
                    }
                })

            }
        }
    }

    private fun showError(error: String = "", show: Boolean = true)
    {
        errorView.text = error
        errorView.visibility = if (show) View.VISIBLE else View.GONE
        scrollView.fullScroll(ScrollView.FOCUS_UP)
    }


    private fun isAFieldEmpty(): Boolean
    {
        return email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || firstName.isEmpty() || description.isEmpty()
    }

    private fun passwordAreDifferent(): Boolean
    {
        return password.textValue != confirmPassword.textValue
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
        return description.textValue.length >= 255
    }


}