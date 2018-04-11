package ecalle.com.bmybank.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import ecalle.com.bmybank.R
import ecalle.com.bmybank.realm.bo.Loan
import ecalle.com.bmybank.view_holders.LoanViewHolder


/**
 * Created by Thomas Ecalle on 07/04/2018.
 */
class LoansAdapter(private var list: List<Loan>, private var onLoanClickListener: OnLoanClickListener) : RecyclerView.Adapter<LoanViewHolder>()
{
    interface OnLoanClickListener
    {
        fun onLoanClick(loan: Loan, userFirstName: String, userLastName: String)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, itemType: Int): LoanViewHolder
    {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.loan, viewGroup, false)
        return LoanViewHolder(view)
    }

    override fun onBindViewHolder(myViewHolder: LoanViewHolder, position: Int)
    {
        val loan = list[position]
        val color = if (position % 2 == 0) R.color.colorPrimary else R.color.colorAccent
        myViewHolder.bind(loan, onLoanClickListener, color)
    }

    override fun getItemCount(): Int
    {
        return list.size
    }

}