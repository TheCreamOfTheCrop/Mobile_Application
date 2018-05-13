package ecalle.com.bmybank.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import ecalle.com.bmybank.*
import ecalle.com.bmybank.realm.RealmServices
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.act
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.startActivity

/**
 * Created by Thomas Ecalle on 16/04/2018.
 */
class ProfileScreenFragment : Fragment(), View.OnClickListener
{
    private lateinit var applicationVersionTextView: TextView
    private lateinit var profile: LinearLayout
    private lateinit var disconnect: LinearLayout


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_profile_screen, container, false)

        applicationVersionTextView = view.find(R.id.applicationVersionTextView)
        profile = view.find(R.id.profile)
        disconnect = view.find(R.id.disconnect)


        profile.setOnClickListener(this)
        disconnect.setOnClickListener(this)
        applicationVersionTextView.text = getString(R.string.application_version, BuildConfig.VERSION_NAME)

        return view
    }

    override fun onClick(view: View?)
    {
        when (view?.id)
        {
            profile.id -> startActivity<ProfileModificationActivity>()
            disconnect.id -> logout()

        }
    }

    private fun logout()
    {

        alert {
            message = getString(R.string.logout_confirmation)
            positiveButton(R.string.yes) {
                val sharedPreferences = ctx.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
                val uid = sharedPreferences.getString(Constants.USER_UUID_PREFERENCES_KEY, "")
                RealmServices.deleteCurrentUser(uid)

                startActivity<LoginActivity>()
                act.finish()
            }

            negativeButton(R.string.no) {}
        }.show()


    }
}