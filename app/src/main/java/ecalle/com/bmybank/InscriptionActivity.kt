package ecalle.com.bmybank

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.stepstone.stepper.StepperLayout
import com.stepstone.stepper.VerificationError
import ecalle.com.bmybank.adapters.InscriptionStepperAdapter
import ecalle.com.bmybank.realm.bo.User
import ecalle.com.bmybank.extensions.log
import ecalle.com.bmybank.interfaces.InscriptionListeningActivity
import kotlinx.android.synthetic.main.activity_inscription.*
import org.jetbrains.anko.alert

/**
 * Created by Thomas Ecalle on 04/03/2018.
 */
class InscriptionActivity :
        AppCompatActivity(),
        StepperLayout.StepperListener,
        InscriptionListeningActivity
{
    private var user: User? = null
    private var avatar: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inscription)

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

        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun onReturn()
    {
        log("on return stepper")
        confirmStoppingInscription()
    }

    override fun onBackPressed()
    {
        log("on back pressed")
        confirmStoppingInscription()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun confirmStoppingInscription()
    {
        alert {
            message = getString(R.string.quitting_inscription_validation)
            positiveButton(R.string.yes) {
                super.onBackPressed()
                finish()
            }

            negativeButton(R.string.no) {}
        }.show()
    }

    override fun onUserInformationsValidated(user: User)
    {
        this.user = user
        log("validated user informations: $user")
    }

    override fun onAvatarSelected(avatarBitMap: Bitmap)
    {
        this.avatar = avatarBitMap
    }

    override fun onAccountValidation(isValid: Boolean)
    {
        this.user?.isAccountValidate = isValid
    }

    override fun getActualUser(): User?
    {
        return this.user
    }

    override fun getAvatar(): Bitmap?
    {
        return this.avatar
    }
}