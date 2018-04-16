package ecalle.com.bmybank.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import ecalle.com.bmybank.BuildConfig
import ecalle.com.bmybank.ProfileModificationActivity
import ecalle.com.bmybank.R
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.startActivity

/**
 * Created by Thomas Ecalle on 16/04/2018.
 */
class ProfileScreenFragment : Fragment(), View.OnClickListener
{
    private lateinit var applicationVersionTextView: TextView
    private lateinit var profile: LinearLayout


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_profile_screen, container, false)

        applicationVersionTextView = view.find(R.id.applicationVersionTextView)
        profile = view.find(R.id.profile)


        profile.setOnClickListener(this)
        applicationVersionTextView.text = getString(R.string.application_version, BuildConfig.VERSION_NAME)

        return view
    }

    override fun onClick(view: View?)
    {
        when (view?.id)
        {
            profile.id -> startActivity<ProfileModificationActivity>()
        }
    }
}