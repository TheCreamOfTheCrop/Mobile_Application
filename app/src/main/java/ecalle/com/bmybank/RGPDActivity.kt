package ecalle.com.bmybank

import android.os.Bundle
import android.support.v4.app.ShareCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import ecalle.com.bmybank.custom_components.BeMyDialog
import ecalle.com.bmybank.extensions.customAlert
import ecalle.com.bmybank.realm.RealmServices
import ecalle.com.bmybank.realm.bo.User
import ecalle.com.bmybank.services.BmyBankApi
import ecalle.com.bmybank.services_responses_bo.GettingUserLoansResponse
import ecalle.com.bmybank.services_responses_bo.NoteListReponse
import kotlinx.android.synthetic.main.activity_rgpd.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Thomas Ecalle on 08/07/2018.
 */
class RGPDActivity : AppCompatActivity(), View.OnClickListener
{
    private var loader: BeMyDialog? = null
    private var stringEmailBuilder = StringBuilder()
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rgpd)

        user = RealmServices.getCurrentUser(this)


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
                loader = customAlert(BeMyDialog.TYPE.LOADING, message = R.string.loading_personal_data)
                getAllDataFromUser()
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

        val shareBuilder = ShareCompat.IntentBuilder.from(this)
        shareBuilder.setType("message/rfc822")
        shareBuilder.addEmailTo(Constants.ADMIN_EMAIL_ADRESS)
        shareBuilder.setSubject(getString(R.string.asking_for_deletion))
        shareBuilder.setText(getString(R.string.delting_id, user?.email))

        val pendingIntent = shareBuilder.intent

        if (pendingIntent.resolveActivity(packageManager) != null)
        {
            startActivity(pendingIntent)
        }
    }

    private fun getAllDataFromUser()
    {
        val api = BmyBankApi.getInstance(this)
        val findPersonalLoansRequest = api.findPersonalLoans()

        findPersonalLoansRequest.enqueue(object : Callback<GettingUserLoansResponse>
        {
            override fun onResponse(call: Call<GettingUserLoansResponse>?, response: Response<GettingUserLoansResponse>?)
            {
                val res = response?.body()
                if (res != null && res.success)
                {
                    stringEmailBuilder.append("\nVos prêts :\n\n")
                    res.loans.forEach {

                        stringEmailBuilder.append("Montant : ${it.amount}\n")
                        stringEmailBuilder.append("Taux d'intérêt : ${it.rate}\n")
                        stringEmailBuilder.append("Durée : ${it.delay}\n")
                        stringEmailBuilder.append("Etat : ${it.state_id}\n")

                        stringEmailBuilder.append("Vous êtes :" + if (it.user_requester_id == user?.id) "Demandeur" else "Prêteur\n")
                        stringEmailBuilder.append("\n-------------------\n")
                    }
                    stringEmailBuilder.append("\n-------------------------------------------\n")

                    getNotes()
                }
            }

            override fun onFailure(call: Call<GettingUserLoansResponse>?, t: Throwable?)
            {
                loader?.dismiss()
                toast(R.string.server_issue)
            }

        })
    }

    private fun getNotes()
    {
        val api = BmyBankApi.getInstance(this)
        val notesRequest = api.getNotes(user?.id!!)

        notesRequest.enqueue(object : Callback<NoteListReponse>
        {
            override fun onFailure(call: Call<NoteListReponse>?, t: Throwable?)
            {
                loader?.dismiss()
                toast(R.string.server_issue)
            }

            override fun onResponse(call: Call<NoteListReponse>?, response: Response<NoteListReponse>?)
            {
                val res = response?.body()
                if (res != null && res.success)
                {
                    stringEmailBuilder.append("\nLes notes que l'on vous a attribué :\n\n")
                    res.note.forEach {

                        stringEmailBuilder.append("Note : ${it.note}\n")
                        stringEmailBuilder.append("Commentaire : ${it.comments}\n")

                        stringEmailBuilder.append("\n-------------------\n")
                    }
                    stringEmailBuilder.append("\n-------------------------------------------\n")


                    getCreatedNote()
                }
            }

        })
    }

    private fun getCreatedNote()
    {
        val api = BmyBankApi.getInstance(this)
        val notesRequest = api.getCreatedNotes()

        notesRequest.enqueue(object : Callback<NoteListReponse>
        {
            override fun onFailure(call: Call<NoteListReponse>?, t: Throwable?)
            {
                loader?.dismiss()
                toast(R.string.server_issue)
            }

            override fun onResponse(call: Call<NoteListReponse>?, response: Response<NoteListReponse>?)
            {
                val res = response?.body()
                if (res != null && res.success)
                {
                    stringEmailBuilder.append("\nLes notes que vous avez donné :\n\n")
                    res.note.forEach {

                        stringEmailBuilder.append("Note : ${it.note}\n")
                        stringEmailBuilder.append("Commentaire : ${it.comments}\n")

                        stringEmailBuilder.append("\n-------------------\n")
                    }
                    stringEmailBuilder.append("\n-------------------------------------------\n")


                    writeInMail()
                }
            }

        })

    }

    private fun writeInMail()
    {
        loader?.dismiss()

        val shareBuilder = ShareCompat.IntentBuilder.from(this)
        shareBuilder.setType("message/rfc822")
        shareBuilder.addEmailTo(user?.email)
        shareBuilder.setSubject(getString(R.string.your_personal_data_email_subject))
        shareBuilder.setText(stringEmailBuilder.toString())

        val pendingIntent = shareBuilder.intent

        if (pendingIntent.resolveActivity(packageManager) != null)
        {
            startActivity(pendingIntent)
        }
        stringEmailBuilder.delete(0, stringEmailBuilder.length)

    }
}