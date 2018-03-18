package ecalle.com.bmybank.fragments.inscription_steps

import android.os.Bundle
import android.os.Handler
import android.support.annotation.UiThread
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.stepstone.stepper.BlockingStep
import com.stepstone.stepper.StepperLayout
import com.stepstone.stepper.VerificationError
import ecalle.com.bmybank.R
import ecalle.com.bmybank.extensions.log

/**
 * Created by Thomas Ecalle on 04/03/2018.
 */
class ScannerStep : Fragment(), BlockingStep
{

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_identity_scanner_step, container, false)

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

    @UiThread
    override fun onBackClicked(callback: StepperLayout.OnBackClickedCallback?)
    {
        log("Scanner Step on Back clicked")
        callback?.goToPrevStep()
    }

    override fun onCompleteClicked(callback: StepperLayout.OnCompleteClickedCallback?)
    {
        log("Scanner Step on Complete clicked")
        callback?.stepperLayout?.showProgress(getString(R.string.inscription_loading_message))
        Handler().postDelayed(
                {
                    callback?.stepperLayout?.hideProgress()
                    log("Scanner Step finished loading")
                    callback?.complete()
                },
                2000L)
    }

    @UiThread
    override fun onNextClicked(callback: StepperLayout.OnNextClickedCallback?)
    {
        log("Scanner Step on Next clicked")
    }

}