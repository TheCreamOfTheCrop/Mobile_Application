package ecalle.com.bmybank

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.EditText
import com.tapadoo.alerter.Alerter
import ecalle.com.bmybank.bo.LoginResponse
import ecalle.com.bmybank.custom_components.BeMyDialog
import ecalle.com.bmybank.extensions.alertError
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
    private var loadingDialog: BeMyDialog? = null

    companion object
    {
        val INSCRIPTION_REQUEST = 42
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login = find(R.id.login)
        password = find(R.id.password)

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
            passwordForgotten.id ->
            {
                //startActivity<PasswordForgottenActivity>()
            }
            inscription.id -> startActivityForResult<InscriptionActivity>(INSCRIPTION_REQUEST)
            validate.id -> login()
        }
    }

    private fun login()
    {
        loadingDialog = customAlert(message = R.string.connection_loading, type = BeMyDialog.TYPE.LOADING)


        val api = BmyBankApi.getInstance(this)
        val loginRequest = api.login(login.textValue, password.textValue)


        loginRequest.enqueue(object : Callback<LoginResponse>
        {
            override fun onResponse(call: Call<LoginResponse>, andRegisterResponse: Response<LoginResponse>)
            {
                loadingDialog?.dismiss()

                if (andRegisterResponse.code() == 400)
                {
                    showError()
                }
                else
                {
                    showError(show = false)
                    val loginResponse = andRegisterResponse.body()
                    if (loginResponse?.result?.user != null)
                    {
                        val user = loginResponse.result.user
                        saveUser(user, loginResponse.result.token)
                        goToMainActivity()
                    }
                    else
                    {
                        toast(R.string.server_issue)
                    }
                }


            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable)
            {
                //toast("Failure getting user from server, throwable message : ${t.message}")
                loadingDialog?.dismiss()
                showError(R.string.not_internet)
            }
        })

        log("end loading function")

    }

    private fun saveUser(user: User, token: String)
    {
        val sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(Constants.USER_UUID_PREFERENCES_KEY, user.uid).apply()
        sharedPreferences.edit().putString(Constants.TOKEN_PREFERENCES_KEY, token).apply()

        RealmServices.saveCurrentuser(user)
    }

    private fun goToMainActivity()
    {
        loadingDialog?.dismiss()

        startActivity<MainActivity>()
        finish()
    }

    private fun showError(message: Int = R.string.wrong_email_or_password, show: Boolean = true)
    {
        alertError(getString(message))
    }
}

