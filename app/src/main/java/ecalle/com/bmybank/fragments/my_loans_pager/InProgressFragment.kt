package ecalle.com.bmybank.fragments.my_loans_pager

import ecalle.com.bmybank.Constants
import ecalle.com.bmybank.R
import ecalle.com.bmybank.adapters.LoansAdapter
import ecalle.com.bmybank.extensions.toast
import ecalle.com.bmybank.fragments.LoadingLoansFragment
import ecalle.com.bmybank.realm.bo.Loan

/**
 * Created by Thomas Ecalle on 03/04/2018.
 */
class InProgressFragment : LoadingLoansFragment(), LoansAdapter.OnLoanClickListener
{
    override fun onLoanClick(loan: Loan, userFirstName: String, userLastName: String)
    {
        toast("clicked on loan with id : ${loan.id}")
    }

    override fun getLoanClickListener(): LoansAdapter.OnLoanClickListener
    {
        return this
    }

    override fun load()
    {
        loadThenGetLoans()
    }

    override fun getLoansType(): String
    {
        return Constants.IN_PROGRESS_LOANS
    }
}
