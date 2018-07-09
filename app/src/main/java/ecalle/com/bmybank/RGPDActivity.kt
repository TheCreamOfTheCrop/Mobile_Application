package ecalle.com.bmybank

import android.os.Bundle
import android.support.v4.app.ShareCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import ecalle.com.bmybank.custom_components.BeMyDialog
import ecalle.com.bmybank.extensions.alertError
import ecalle.com.bmybank.extensions.customAlert
import ecalle.com.bmybank.realm.RealmServices
import ecalle.com.bmybank.services.BmyBankApi
import ecalle.com.bmybank.services_responses_bo.SImpleResponse
import kotlinx.android.synthetic.main.activity_rgpd.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Thomas Ecalle on 08/07/2018.
 */
class RGPDActivity : AppCompatActivity(), View.OnClickListener
{

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rgpd)

        exit.setOnClickListener(this)
        delete.setOnClickListener(this)
        sendByMail.setOnClickListener(this)
    }

    override fun onClick(view: View?)
    {

        when (view?.id)
        {
            exit.id -> onBackPressed()
            sendByMail.id ->
            {
                val user = RealmServices.getCurrentUser(this)

                val shareBuilder = ShareCompat.IntentBuilder.from(this)
                shareBuilder.setType("message/rfc822")
                shareBuilder.addEmailTo(user?.email)
                shareBuilder.setSubject(getString(R.string.your_personal_data_email_subject))
                shareBuilder.setText(getString(R.string.personnal_data_email_body, user?.firstname, user?.lastname, user?.email))

                val pendingIntent = shareBuilder.intent

                if (pendingIntent.resolveActivity(packageManager) != null)
                {
                    startActivity(pendingIntent)
                }
            }
            delete.id ->
            {
                confirmDeletingUser()
            }
        }
    }

    private fun confirmDeletingUser()
    {
        alert {
            message = getString(R.string.confirm_deleting_user)
            positiveButton(R.string.yes) {
                deleteUser()
            }

            negativeButton(R.string.no) {}
        }.show()
    }

    private fun deleteUser()
    {
        val currentUser = RealmServices.getCurrentUser(this) ?: return

        val api = BmyBankApi.getInstance(this)
        val deleteUserRequest = api.deleteUser(currentUser.id)

        val loading = customAlert(type = BeMyDialog.TYPE.LOADING, message = R.string.deleting_user_loading)

        deleteUserRequest.enqueue(object : Callback<SImpleResponse>
        {
            override fun onFailure(call: Call<SImpleResponse>?, t: Throwable?)
            {
                loading.dismiss()
                alertError(getString(R.string.server_issue))
            }

            override fun onResponse(call: Call<SImpleResponse>?, response: Response<SImpleResponse>?)
            {
                loading.dismiss()

                val res = response?.body()
                if (res != null && res.success)
                {
                    RealmServices.deleteCurrentUser(currentUser.uid)
                    startActivity<LoginActivity>()
                    toast(R.string.deleting_data_success)
                    finish()
                }
            }

        })


    }

}