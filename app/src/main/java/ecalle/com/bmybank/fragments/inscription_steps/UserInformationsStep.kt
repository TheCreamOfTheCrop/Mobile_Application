package ecalle.com.bmybank.fragments.inscription_steps

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.stepstone.stepper.Step
import com.stepstone.stepper.VerificationError
import ecalle.com.bmybank.R
import org.jetbrains.anko.support.v4.toast

/**
 * Created by thoma on 04/03/2018.
 */
class UserInformationsStep : Fragment(), Step
{
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater?.inflate(R.layout.fragment_user_information_step, container, false)

        return view
    }

    override fun onSelected()
    {
    }

    override fun verifyStep(): VerificationError?
    {

        return null
    }

    override fun onError(error: VerificationError)
    {
    }

}