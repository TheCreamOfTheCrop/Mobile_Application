package ecalle.com.bmybank.view_holders

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import ecalle.com.bmybank.Constants
import ecalle.com.bmybank.R
import ecalle.com.bmybank.adapters.PublicLoansAdapter
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
class PublicLoanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
{
    private val amount: TextView = itemView.find(R.id.amount)
    private var firsName: String? = null
    private var lastName: String? = null
    private val rate: TextView = itemView.find(R.id.rate)
    private val percentSymbol: ImageView = itemView.find(R.id.percentSymbol)
    private val repayment: TextView = itemView.find(R.id.repayment)

    fun bind(loan: Loan, onLoanClickListener: PublicLoansAdapter.OnPublicLoanClickListener)
    {
        amount.text = loan.amount.toString()
        rate.text = loan.rate.toString()


        if (loan.rate > Constants.BAD_RATE && loan.rate <= Constants.NORMAL_RATE)
        {
            rate.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAccent))
            percentSymbol.setColorFilter(ContextCompat.getColor(itemView.context, R.color.colorAccent))
        }
        else if (loan.rate > Constants.NORMAL_RATE)
        {
            rate.setTextColor(ContextCompat.getColor(itemView.context, R.color.green))
            percentSymbol.setColorFilter(ContextCompat.getColor(itemView.context, R.color.green))
        }



        repayment.text = itemView.context.getString(R.string.repayment_loan_item_label, loan.delay)

        itemView.setOnClickListener {
            onLoanClickListener.onPublicLoanClick(loan, firsName, lastName)
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
                    firsName = user.firstname
                    lastName = user.lastname
                }
            }


            override fun onFailure(call: Call<UserResponse>, t: Throwable)
            {
                println("ok")
            }
        })
    }
}