package ecalle.com.bmybank.fragments.inscription_steps

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import com.stepstone.stepper.Step
import com.stepstone.stepper.VerificationError
import ecalle.com.bmybank.R
import ecalle.com.bmybank.extensions.hasOnlyLetters
import ecalle.com.bmybank.extensions.isEmpty
import ecalle.com.bmybank.extensions.log
import ecalle.com.bmybank.extensions.textValue
import ecalle.com.bmybank.interfaces.InscriptionListeningActivity
import ecalle.com.bmybank.realm.bo.User
import org.jetbrains.anko.find


/**
 * Created by Thomas Ecalle on 04/03/2018.
 */
class UserInformationsStep : Fragment(), Step
{

    lateinit private var scrollView: ScrollView
    lateinit private var email: EditText
    lateinit private var password: EditText
    lateinit private var confirmPassword: EditText
    lateinit private var firstName: EditText
    lateinit private var lastName: EditText
    lateinit private var description: EditText
    lateinit private var errorView: TextView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_user_information_step, container, false)

        scrollView = view.find<ScrollView>(R.id.scrollView)
        errorView = view.find<TextView>(R.id.errorView)
        email = view.find<EditText>(R.id.email)
        password = view.find<EditText>(R.id.password)
        confirmPassword = view.find<EditText>(R.id.confirmPassword)
        firstName = view.find<EditText>(R.id.firstName)
        lastName = view.find<EditText>(R.id.lastName)
        description = view.find<EditText>(R.id.description)

        makeDescriptionScrollable()

        return view
    }

    private fun makeDescriptionScrollable()
    {
        description.setVerticalScrollBarEnabled(true)
        description.setOverScrollMode(View.OVER_SCROLL_ALWAYS)
        description.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET)
        description.setMovementMethod(ScrollingMovementMethod.getInstance())

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

    override fun onSelected()
    {
        log("onSelected UserInformation")
    }

    override fun verifyStep(): VerificationError?
    {
        errorView.visibility = View.GONE

        log("Voici les etats des edit text : \nemail: ${email.text}\npassword: ${password.text}\n" +
                "confirmPassword: ${confirmPassword.text}\nfirstName: ${firstName.text}\nlastName: ${lastName.text}" +
                "\ndescription: ${description.text}" +
                "\nPasswords are equals ? ${password.textValue == confirmPassword.textValue}")

        if (isAFieldEmpty())
        {
            return VerificationError(getString(R.string.form_all_fields_not_complete))
        }
        if (isNotValidEmailAdress())
        {
            return VerificationError(getString(R.string.not_valid_email_adress))
        }
        if (passwordAreDifferent())
        {
            return VerificationError(getString(R.string.passwords_not_equals))
        }
        if (namesAreNotWellFormat())
        {
            return VerificationError(getString(R.string.not_well_format_names))
        }
        if (descriptionIsNotWellFormat())
        {
            return VerificationError(getString(R.string.not_well_format_description))
        }

        val listeningActivity = activity as InscriptionListeningActivity
        val user = User(email = email.textValue, password = password.textValue, lastname = lastName.textValue, firstname = firstName.textValue, description = description.textValue)
        listeningActivity.onUserInformationsValidated(user)

        return null
    }

    override fun onError(error: VerificationError)
    {
        errorView.text = error.errorMessage
        errorView.visibility = View.VISIBLE
        scrollView.fullScroll(ScrollView.FOCUS_UP);
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