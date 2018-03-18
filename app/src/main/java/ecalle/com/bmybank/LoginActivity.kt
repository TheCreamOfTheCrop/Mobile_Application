package ecalle.com.bmybank

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.EditText
import android.widget.TextView
import ecalle.com.bmybank.bo.LoginAndRegisterResponse
import ecalle.com.bmybank.custom_components.BeMyDialog
import ecalle.com.bmybank.extensions.customAlert
import ecalle.com.bmybank.extensions.log
import ecalle.com.bmybank.extensions.textValue
import ecalle.com.bmybank.realm.RealmServices
import ecalle.com.bmybank.realm.bo.User
import ecalle.com.bmybank.services.BmyBankApi
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.find
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity(), View.OnClickListener
{

    lateinit private var login: EditText
    lateinit private var password: EditText
    lateinit private var error: TextView
    private var loadingDialog: BeMyDialog? = null

    companion object
    {
        val INSCRIPTION_REQUEST = 42
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        val currentUserUid = sharedPreferences.getString(Constants.USER_UUID_PREFERENCES_KEY, "")

        if (!currentUserUid.isEmpty())
        {
            log("A user seems already logged in")
            goToMainActivity(currentUserUid)
        }

        login = find(R.id.login)
        password = find(R.id.password)
        error = find(R.id.error)

        login.setText("thomasecalle@hotmail.fr")
        password.setText("totoro")

        passwordForgotten.setOnClickListener(this)
        inscription.setOnClickListener(this)
        validate.setOnClickListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        if (requestCode == INSCRIPTION_REQUEST)
        {
            when (resultCode)
            {
                Activity.RESULT_OK -> customAlert(message = R.string.validated_inscription_message, loop = false)
            }
        }
    }

    override fun onClick(view: View?)
    {
        when (view?.id)
        {
            passwordForgotten.id -> startActivity<PasswordForgottenActivity>()
            inscription.id -> startActivityForResult<InscriptionActivity>(INSCRIPTION_REQUEST)
            validate.id -> login()
        }
    }

    private fun login()
    {
        loadingDialog = customAlert(message = R.string.connection_loading, type = BeMyDialog.TYPE.LOADING)


        val api = BmyBankApi.getInstance()
        val loginRequest = api.login(login.textValue, password.textValue)


        loginRequest.enqueue(object : Callback<LoginAndRegisterResponse>
        {
            override fun onResponse(call: Call<LoginAndRegisterResponse>, andRegisterResponse: Response<LoginAndRegisterResponse>)
            {
                if (andRegisterResponse.code() == 400)
                {
                    showError()
                    loadingDialog?.dismiss()
                }
                else
                {
                    showError(false)
                    val loginResponse = andRegisterResponse.body()
                    if (loginResponse?.user != null)
                    {
                        val user = loginResponse.user
                        saveUser(user)
                        goToMainActivity(user.uid)
                    }
                    else
                    {
                        toast(R.string.server_issue)
                    }
                }


            }

            override fun onFailure(call: Call<LoginAndRegisterResponse>, t: Throwable)
            {
                toast("Failure getting user from server, throwable message : ${t.message}")
            }
        })

        log("end loading function")

    }

    private fun saveUser(user: User)
    {
        val sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(Constants.USER_UUID_PREFERENCES_KEY, user.uid).apply()

        RealmServices.saveCurrentuser(user)
    }

    private fun goToMainActivity(uid: String)
    {
        loadingDialog?.dismiss()

        startActivity<MainActivity>(Constants.SERIALIZED_USER_UID to uid)
        finish()
    }

    private fun showError(show: Boolean = true)
    {
        error.text = getString(R.string.wrong_email_or_password)
        error.visibility = if (show) View.VISIBLE else View.GONE
    }
}

