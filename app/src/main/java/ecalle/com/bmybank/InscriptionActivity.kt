package ecalle.com.bmybank

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import com.stepstone.stepper.StepperLayout
import com.stepstone.stepper.VerificationError
import ecalle.com.bmybank.adapters.InscriptionStepperAdapter
import ecalle.com.bmybank.extensions.log
import kotlinx.android.synthetic.main.activity_inscription.*
import org.jetbrains.anko.find

/**
 * Created by thoma on 04/03/2018.
 */
class InscriptionActivity : AppCompatActivity(), ToolbarManager, StepperLayout.StepperListener
{

    override val toolbar by lazy { find<Toolbar>(R.id.toolbar) }


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inscription)

        toolbarTitle = getString(R.string.inscription)

        stepperLayout.adapter = InscriptionStepperAdapter(supportFragmentManager, this)
        stepperLayout.setListener(this)
    }

    override fun onStepSelected(newStepPosition: Int)
    {
        log("step selected : $newStepPosition")
    }

    override fun onError(verificationError: VerificationError?)
    {
        log("on error: ${verificationError?.errorMessage}")
    }

    override fun onCompleted(completeButton: View?)
    {
        log("on complete stepper")
        finish()
    }

    override fun onReturn()
    {
        log("on return stepper")
        finish()
    }

}