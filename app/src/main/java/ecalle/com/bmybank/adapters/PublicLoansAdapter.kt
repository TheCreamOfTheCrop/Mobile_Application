package ecalle.com.bmybank.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import ecalle.com.bmybank.R
import ecalle.com.bmybank.realm.bo.Loan
import ecalle.com.bmybank.view_holders.PublicLoanViewHolder

/**
 * Created by Thomas Ecalle on 07/04/2018.
 */
class PublicLoansAdapter(private var list: List<Loan>, private var onPublicLoanClickListener: OnPublicLoanClickListener) : RecyclerView.Adapter<PublicLoanViewHolder>()
{

    enum class Color
    {
        BLUE, ORANGE
    }

    interface OnPublicLoanClickListener
    {
        fun onPublicLoanClick(loan: Loan, userFirstName: String?, userLastName: String?)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, itemType: Int): PublicLoanViewHolder
    {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.public_loan_list_item, viewGroup, false)
        return PublicLoanViewHolder(view)
    }

    override fun onBindViewHolder(holder: PublicLoanViewHolder, position: Int)
    {
        val loan = list[position]
        holder.bind(loan, onPublicLoanClickListener)
    }

    override fun getItemCount(): Int
    {
        return list.size
    }

}