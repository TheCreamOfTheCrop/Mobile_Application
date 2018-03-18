package ecalle.com.bmybank

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.EditText
import ecalle.com.bmybank.bo.LoginResponse
import ecalle.com.bmybank.extensions.customAlert
import ecalle.com.bmybank.extensions.log
import ecalle.com.bmybank.extensions.textValue
import ecalle.com.bmybank.services.BmyBankApi
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity(), View.OnClickListener
{

    private val email: EditText? = null
    private val password: EditText? = null

    companion object
    {
        val INSCRIPTION_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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
        val api = BmyBankApi.getInstance()
        val loginRequest = api.login(email?.textValue, password?.textValue)


        loginRequest.enqueue(object : Callback<LoginResponse>
        {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>)
            {
                val loginResponse = response.body()

                toast("login repsonse : $loginResponse")

            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable)
            {
                toast("Failure getting user from server, throwable message : ${t.message}")
            }
        })

        log("end loading function")

    }
}

