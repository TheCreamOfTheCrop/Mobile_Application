package ecalle.com.bmybank

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import org.jetbrains.anko.find

/**
 * Created by thoma on 01/03/2018.
 */
class PasswordForgottenActivity : AppCompatActivity(), ToolbarManager
{
    override val toolbar by lazy { find<Toolbar>(R.id.toolbar) }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_forgotten)

        toolbarTitle = getString(R.string.passwordForgotten)
        enableHomeAsUp { onBackPressed() }
    }
}