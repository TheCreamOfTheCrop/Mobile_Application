package ecalle.com.bmybank

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import ecalle.com.bmybank.custom_components.BeMyDialog
import ecalle.com.bmybank.extensions.alertError
import ecalle.com.bmybank.extensions.customAlert
import ecalle.com.bmybank.extensions.textValue
import ecalle.com.bmybank.services.BmyBankApi
import ecalle.com.bmybank.services_responses_bo.AddNoteResponse
import kotlinx.android.synthetic.main.activity_add_note.*
import kotlinx.android.synthetic.main.custom_lined_title.view.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Thomas Ecalle on 08/07/2018.
 */
class AddNoteActivity : AppCompatActivity(), View.OnClickListener
{

    companion object
    {
        const val USER_ID_KEY = "userIdKey"
        const val LOAN_ID_KEY = "loanIdKey"
    }

    private val userId by lazy { intent.getIntExtra(AddNoteActivity.USER_ID_KEY, -1) }
    private val loanId by lazy { intent.getIntExtra(AddNoteActivity.LOAN_ID_KEY, -1) }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        if (intent == null || intent.getIntExtra(AddNoteActivity.USER_ID_KEY, -1) == -1)
        {
            finish()
            return
        }

        if (intent == null || intent.getIntExtra(AddNoteActivity.LOAN_ID_KEY, -1) == -1)
        {
            finish()
            return
        }


        validate.setOnClickListener(this)
    }

    override fun onClick(view: View?)
    {

        when (view?.id)
        {
            validate.id -> addNote()
        }
    }

    private fun addNote()
    {
        val note = ratingBar.rating
        val comment = comment.textValue


        val customAlert = customAlert(type = BeMyDialog.TYPE.LOADING, message = R.string.adding_note_loading)

        val api = BmyBankApi.getInstance(ctx)

        val addNoteRequest = api.addNote(note, comment, userId, loanId)

        addNoteRequest.enqueue(object : Callback<AddNoteResponse>
        {
            override fun onFailure(call: Call<AddNoteResponse>?, t: Throwable?)
            {
                customAlert.dismiss()
                toast(R.string.server_issue)
            }

            override fun onResponse(call: Call<AddNoteResponse>?, response: Response<AddNoteResponse>?)
            {
                customAlert.dismiss()
                val res = response?.body()
                if (res != null && res.success)
                {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                else
                {
                    alertError(getString(R.string.fields_error))
                }
            }

        })

    }
}