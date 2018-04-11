package ecalle.com.bmybank.view_holders

import android.support.annotation.ColorRes
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import ecalle.com.bmybank.R
import ecalle.com.bmybank.adapters.LoansAdapter
import ecalle.com.bmybank.bo.UserResponse
import ecalle.com.bmybank.realm.bo.Loan
import ecalle.com.bmybank.services.BmyBankApi
import org.jetbrains.anko.find
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * Created by Thomas Ecalle on 07/04/2018.
 */
class LoanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
{
    private val amount: TextView = itemView.find(R.id.amount)
    private val firsName: TextView = itemView.find(R.id.firstName)
    private val lastName: TextView = itemView.find(R.id.lastName)
    private val rate: TextView = itemView.find(R.id.rate)
    private val waveLayout: ConstraintLayout = itemView.find(R.id.waveLayout)
    private val repayment: TextView = itemView.find(R.id.repayment)
    private val loader: ProgressBar = itemView.find(R.id.loader)

    //puis ajouter une fonction pour remplir la cellule en fonction d'un MyObject
    fun bind(loan: Loan, onLoanClickListener: LoansAdapter.OnLoanClickListener, @ColorRes color: Int)
    {
        amount.text = loan.amount.toString()
        rate.text = loan.rate.toString()
        //description.text = loan.description
        repayment.text = itemView.context.getString(R.string.repayment_loan_item_label, loan.delay)

        /*
        val waveDrawable = ContextCompat.getDrawable(itemView.context, R.drawable.vague)
        DrawableCompat.setTint(waveDrawable!!, ContextCompat.getColor(itemView.context, color))
        waveLayout.background = waveDrawable
           */

        itemView.setOnClickListener {
            onLoanClickListener.onLoanClick(loan, firsName.text.toString(), lastName.text.toString())
        }

        val api = BmyBankApi.getInstance(itemView.context)
        val findUserByIdRequest = api.findUserById(loan.user_requester_id)

        findUserByIdRequest.enqueue(object : Callback<UserResponse>
        {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>)
            {
                val userResponse = response.body()
                if (userResponse?.success != null && userResponse?.success)
                {
                    val user = userResponse.user
                    firsName.text = user.firstname
                    lastName.text = user.lastname

                    loader.visibility = View.GONE
                    firsName.visibility = View.VISIBLE
                    lastName.visibility = View.VISIBLE
                }
            }


            override fun onFailure(call: Call<UserResponse>, t: Throwable)
            {
                println("ok")
            }
        })
    }
}