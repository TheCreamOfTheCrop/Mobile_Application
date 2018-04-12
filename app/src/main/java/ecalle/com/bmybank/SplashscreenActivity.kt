package ecalle.com.bmybank

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import ecalle.com.bmybank.extensions.log
import ecalle.com.bmybank.realm.RealmServices
import org.jetbrains.anko.startActivity

/**
 * Created by Thomas Ecalle on 12/04/2018.
 */
class SplashscreenActivity : AppCompatActivity()
{

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splashscreen)

        Handler().postDelayed({

            checkForUserTheNavigate()

        }, 2000)


    }

    private fun checkForUserTheNavigate()
    {
        if (RealmServices.getCurrentUser(this) != null)
        {
            log("A user seems already logged in")
            goToMainActivity()
        }
        else
        {
            log("No user already logged in")
            goToLoginActivity()
        }
    }

    private fun goToMainActivity()
    {
        startActivity<MainActivity>()
        finish()
    }

    private fun goToLoginActivity()
    {
        startActivity<LoginActivity>()
        finish()
    }
}
