package ecalle.com.bmybank.fragments

import android.os.Bundle
import android.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ecalle.com.bmybank.R

/**
 * Created by Thomas Ecalle on 18/03/2018.
 */
class PublicProfileFragment : Fragment()
{

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_public_profile, container, false)

        return view
    }
}