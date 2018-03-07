package ecalle.com.bmybank.fragments.inscription_steps

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
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
import org.jetbrains.anko.find

/**
 * Created by thoma on 04/03/2018.
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

        return view
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
                "\ndescription: ${description.text}")

        if (isAFieldEmpty())
        {
            return VerificationError(getString(R.string.form_all_fields_not_complete))
        }
        else if (isNotValidEmailAdress())
        {
            return VerificationError(getString(R.string.not_valid_email_adress))
        }
        else if (passwordAreDifferent())
        {
            return VerificationError(getString(R.string.passwords_not_equals))
        }
        else if (namesAreNotWellFormat())
        {
            return VerificationError(getString(R.string.not_well_format_names))
        }

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
        return password.text != confirmPassword.text
    }

    private fun isNotValidEmailAdress(): Boolean
    {
        return !android.util.Patterns.EMAIL_ADDRESS.matcher(email.text).matches();
    }

    private fun namesAreNotWellFormat(): Boolean
    {
        return !firstName.hasOnlyLetters() || !lastName.hasOnlyLetters()
    }

}