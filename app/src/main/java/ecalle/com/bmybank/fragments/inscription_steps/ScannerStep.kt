package ecalle.com.bmybank.fragments.inscription_steps

import android.graphics.Bitmap
import android.os.Bundle
import android.support.annotation.UiThread
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.stepstone.stepper.BlockingStep
import com.stepstone.stepper.StepperLayout
import com.stepstone.stepper.VerificationError
import ecalle.com.bmybank.Constants
import ecalle.com.bmybank.InscriptionActivity
import ecalle.com.bmybank.R
import ecalle.com.bmybank.custom_components.BeMyDialog
import ecalle.com.bmybank.extensions.customAlert
import ecalle.com.bmybank.extensions.log
import ecalle.com.bmybank.extensions.toast
import ecalle.com.bmybank.realm.bo.User
import ecalle.com.bmybank.services.BmyBankApi
import ecalle.com.bmybank.services_respnses_bo.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream


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

        val listeningActivity = activity as InscriptionActivity

        val actualUser = listeningActivity.getActualUser()
        val avatar = listeningActivity.getAvatar()

        sendAvatarToFirebase(avatar, actualUser)

        val api = BmyBankApi.getInstance(context)
        val registerRequest = api.register(actualUser?.email, actualUser?.password, actualUser?.lastname, actualUser?.firstname, actualUser?.description, actualUser?.isAccountValidate)

        registerRequest.enqueue(object : Callback<UserResponse>
        {
            override fun onFailure(call: Call<UserResponse>?, t: Throwable?)
            {
                callback?.stepperLayout?.hideProgress()
                log("Scanner Step finished loading")
                toast("error while register")
            }

            override fun onResponse(call: Call<UserResponse>?, response: Response<UserResponse>?)
            {
                callback?.stepperLayout?.hideProgress()
                log("Scanner Step finished loading")
                if (response?.code() == 400)
                {
                    customAlert(message = R.string.email_already_used, type = BeMyDialog.TYPE.FAILURE, loop = false)
                }
                else
                {
                    callback?.complete()
                }
            }

        })
    }

    private fun sendAvatarToFirebase(bitmap: Bitmap?, user: User?)
    {
        var emailWithoutSpecialCharacters = user?.email?.replace("@", "")
        emailWithoutSpecialCharacters = emailWithoutSpecialCharacters?.replace(".", "")
        emailWithoutSpecialCharacters.plus(".jpg")

        if (bitmap != null && emailWithoutSpecialCharacters != null)
        {
            val firebaseStorage = FirebaseStorage.getInstance()

            val reference = firebaseStorage.getReference(Constants.PROFILE_PICTURES_NODE).child(emailWithoutSpecialCharacters)

            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            val uploadTask = reference.putBytes(data)
            uploadTask.addOnFailureListener(OnFailureListener {
                //toast("failure adding image")
            }).addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> {
                //toast("successfully added image !")
            })
        }
    }

    @UiThread
    override fun onNextClicked(callback: StepperLayout.OnNextClickedCallback?)
    {
        log("Scanner Step on Next clicked")
    }

}