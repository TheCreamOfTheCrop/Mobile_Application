package ecalle.com.bmybank.fragments.inscription_steps

import android.app.Fragment
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
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
import ecalle.com.bmybank.extensions.*
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
      isNotValidEmailAdress()      -> showInformation(getString(R.string.not_valid_email_adress))
      passwordAreDifferent()       -> showInformation(getString(R.string.passwords_not_equals))
      namesAreNotWellFormat()      -> showInformation(getString(R.string.not_well_format_names))
      descriptionIsNotWellFormat() -> showInformation(getString(R.string.not_well_format_description))
      else                         ->
      {
        var needToUpdatePassword = false
        var needToUpdateEmail = false

        if (email.textValue != user.email)
        {
          needToUpdateEmail = true
          user.email = email.textValue
        }

        if (!password.textValue.isEmpty())
        {
          needToUpdatePassword = true
        }

        user.lastname = lastName.textValue
        user.firstname = firstName.textValue
        user.description = description.textValue

        loadingDialog = customAlert(message = R.string.user_updating_loading, type = BeMyDialog.TYPE.LOADING)


        val api = BmyBankApi.getInstance()

        val updateRequest =
            api.updateUser(id = user.id,
                email = if (needToUpdateEmail) user.email else null,
                previousPassword = if (needToUpdatePassword) user.password else null,
                newPassword = if (needToUpdatePassword) password.textValue else null,
                lastname = user.lastname,
                firstname = user.firstname,
                description = user.description)

        updateRequest.enqueue(object : Callback<LoginAndRegisterResponse>
        {
          override fun onResponse(call: Call<LoginAndRegisterResponse>, response: Response<LoginAndRegisterResponse>)
          {
            if (response.code() == 400)
            {
              if (response.errorBody() != null)
              {
                showInformation(response.errorBody()!!.string())
              }
              else
              {
                showInformation(getString(R.string.impossible_user_update))
              }
              loadingDialog?.dismiss()
            }
            else
            {
              showInformation(information = getString(R.string.user_update_success), error = false)

              RealmServices.saveCurrentuser(user)

              loadingDialog?.dismiss()

            }

            fillInformations()

          }

          override fun onFailure(call: Call<LoginAndRegisterResponse>, t: Throwable)
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
    val color = if (error) ContextCompat.getColor(activity, R.color.red) else ContextCompat.getColor(activity, R.color.green)
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
    return description.textValue.length >= 255
  }


}