package ecalle.com.bmybank

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.EditText
import android.widget.TextView
import ecalle.com.bmybank.bo.LoginResponse
import ecalle.com.bmybank.custom_components.BeMyDialog
import ecalle.com.bmybank.extensions.customAlert
import ecalle.com.bmybank.extensions.log
import ecalle.com.bmybank.extensions.textValue
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
                Activity.RESULT_OK -> customAlert(message = R.string.validated_inscription_message)
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
        val loadingDialog = customAlert(message = R.string.connection_loading, type = BeMyDialog.TYPE.LOADING)


        val api = BmyBankApi.getInstance()
        val loginRequest = api.login(login.textValue, password.textValue)


        loginRequest.enqueue(object : Callback<LoginResponse>
        {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>)
            {
                if (response.code() == 400)
                {
                    showError()
                }
                else
                {
                    showError(false)
                    val loginResponse = response.body()
                    if (loginResponse?.user != null)
                    {
                        loadingDialog.dismiss()
                        goToMainActivity(loginResponse)
                    }
                    else
                    {
                        toast(R.string.server_issue)
                    }
                }


            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable)
            {
                toast("Failure getting user from server, throwable message : ${t.message}")
            }
        })

        log("end loading function")

    }

    private fun goToMainActivity(loginResponse: LoginResponse)
    {
        startActivity<MainActivity>(Constants.SERIALIZED_OBJECT_KEY to loginResponse.user)
        finish()
    }

    private fun showError(show: Boolean = true)
    {
        error.text = getString(R.string.wrong_email_or_password)
        error.visibility = if (show) View.VISIBLE else View.GONE
    }
}

