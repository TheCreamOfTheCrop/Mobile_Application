package ecalle.com.bmybank

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import ecalle.com.bmybank.extensions.customAlert
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult

class LoginActivity : AppCompatActivity(), View.OnClickListener
{

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
        }
    }
}
