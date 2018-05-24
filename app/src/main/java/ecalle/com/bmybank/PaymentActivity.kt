package ecalle.com.bmybank

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import ecalle.com.bmybank.custom_components.BeMyDialog
import ecalle.com.bmybank.extensions.customAlert
import ecalle.com.bmybank.extensions.log
import ecalle.com.bmybank.firebase.Utils
import ecalle.com.bmybank.realm.RealmServices
import ecalle.com.bmybank.realm.bo.Loan
import ecalle.com.bmybank.realm.bo.User
import ecalle.com.bmybank.services.BmyBankApi
import ecalle.com.bmybank.services_respnses_bo.LydiaPaymentInitResponse
import ecalle.com.bmybank.services_respnses_bo.Payment
import ecalle.com.bmybank.services_respnses_bo.UserResponse
import org.jetbrains.anko.find
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


/**
 * Created by Thomas Ecalle on 23/05/2018.
 */
class PaymentActivity : AppCompatActivity(), View.OnClickListener
{

    private lateinit var amount: TextView
    private lateinit var success: TextView
    private lateinit var failure: TextView
    private lateinit var email: EditText
    private lateinit var send: Button
    private lateinit var exit: FloatingActionButton
    private lateinit var loan: Loan
    private var otherUser: User? = null
    private lateinit var currentUser: User
    private lateinit var loader: BeMyDialog
    private lateinit var identifier: String


    companion object
    {
        val LOAN_KEY = "loanKey"
        val OTHER_USER_KEY = "otherUserKey"
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        amount = find(R.id.amount)
        email = find(R.id.email)
        send = find(R.id.send)
        exit = find(R.id.exit)
        success = find(R.id.success)
        failure = find(R.id.failure)

        loan = intent.getSerializableExtra(LOAN_KEY) as Loan
        otherUser = intent.getSerializableExtra(OTHER_USER_KEY) as User?

        currentUser = RealmServices.getCurrentUser(this)!!

        exit.setOnClickListener(this)
        send.setOnClickListener(this)

        amount.text = loan.amount.toString()
        email.setText(currentUser.email)

    }

    override fun onClick(view: View?)
    {
        when (view?.id)
        {
            exit.id -> onBackPressed()
            send.id -> validate()
        }
    }

    private fun validate()
    {
        loader = customAlert(message = R.string.loading_lydia, type = BeMyDialog.TYPE.LOADING)

        if (otherUser == null)
        {
            findUserInformations()
        }
        else
        {
            goToLydia()
        }
    }

    private fun goToLydia()
    {
        val api = BmyBankApi.getInstance(this)

        val time = Calendar.getInstance().timeInMillis
        identifier = "${loan.id}$time"

        log("sent to lydia : ${email.text},${otherUser?.email},${loan.amount},,${loan.description} with identifier : $identifier")

        val realAmount: Float = if (loan.amount > 1000) 1000f else loan.amount

        val lydiaRequest = api.initLydiaPayment(
                otherUserEmailOrPhone = otherUser?.email!!,
                currentUserEmailOrPhone = currentUser.email,
                successUrl = getString(R.string.lydia_success_url, identifier, realAmount, currentUser.id),
                failUrl = getString(R.string.lydia_failure_url, identifier, realAmount, currentUser.id),
                amount = realAmount.toString(),
                message = loan.description.toString())


        lydiaRequest.enqueue(object : Callback<LydiaPaymentInitResponse>
        {
            override fun onFailure(call: Call<LydiaPaymentInitResponse>?, t: Throwable?)
            {
                toast("failed to join lydia")
                loader.dismiss()

            }

            override fun onResponse(call: Call<LydiaPaymentInitResponse>?, response: Response<LydiaPaymentInitResponse>?)
            {
                val lydiaResponse = response?.body()
                if (lydiaResponse?.error == "0")
                {
                    val lydiaUrl = lydiaResponse.data.url
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(lydiaUrl)
                    startActivity(intent)
                    lookForAnswer()
                }

                loader.dismiss()

            }

        })


    }

    private fun lookForAnswer()
    {

        Utils.getDatabase().getReference("payments").child(identifier).addChildEventListener(object : ChildEventListener
        {
            override fun onCancelled(error: DatabaseError?)
            {
                log("listening to payments, onCancelled $error")
            }

            override fun onChildMoved(snapshot: DataSnapshot?, p1: String?)
            {
                log("listening to payments, onChildMoved ${snapshot.toString()}")
            }

            override fun onChildChanged(snapshot: DataSnapshot?, p1: String?)
            {
                log("listening to payments, onChildCHanged ${snapshot.toString()}")
            }

            override fun onChildAdded(snapshot: DataSnapshot?, p1: String?)
            {
                val payment = snapshot?.getValue(Payment::class.java)

                log("listening to payments, onChildAdded ${snapshot.toString()}")
                log("getting payment object :  ${payment.toString()}")

                if (payment?.success!!)
                {
                    failure.visibility = View.GONE
                    success.visibility = View.VISIBLE
                }
                else
                {
                    success.visibility = View.GONE
                    failure.visibility = View.VISIBLE
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot?)
            {
                log("listening to payments, onChildReMoved ${snapshot.toString()}")
            }
        })
    }


    private fun findUserInformations()
    {
        val api = BmyBankApi.getInstance(this)
        val findUserByIdRequest = api.findUserById(loan.user_requester_id)


        findUserByIdRequest.enqueue(object : Callback<UserResponse>
        {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>)
            {
                val userResponse = response.body()
                if (userResponse?.success != null && userResponse?.success)
                {
                    otherUser = userResponse.user
                    goToLydia()
                }
            }


            override fun onFailure(call: Call<UserResponse>, t: Throwable)
            {
                toast(R.string.not_internet)
            }
        })

    }
}