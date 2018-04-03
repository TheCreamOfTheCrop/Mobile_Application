package ecalle.com.bmybank.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import ecalle.com.bmybank.Constants
import ecalle.com.bmybank.R
import ecalle.com.bmybank.realm.bo.User
import org.jetbrains.anko.find

/**
 * Created by Thomas Ecalle on 18/03/2018.
 */
class PublicProfileFragment : Fragment()
{

    lateinit private var firstName: TextView
    lateinit private var lastName: TextView
    lateinit private var description: TextView
    lateinit private var avatar: ImageView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_public_profile, container, false)

        firstName = view.find(R.id.firstName)
        lastName = view.find(R.id.lastName)
        description = view.find(R.id.description)
        avatar = view.find(R.id.avatar)

        if (arguments != null)
        {
            val user = arguments?.getSerializable(Constants.SERIALIZED_OBJECT_KEY) as User

            firstName.text = user.firstname
            lastName.text = user.lastname
            description.text = user.description
        }

        return view
    }

}