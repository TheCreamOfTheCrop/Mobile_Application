package ecalle.com.bmybank.fragments.my_loans_pager


import android.content.Intent
import ecalle.com.bmybank.Constants
import ecalle.com.bmybank.LoanViewerActivity
import ecalle.com.bmybank.R
import ecalle.com.bmybank.adapters.LoansAdapter
import ecalle.com.bmybank.extensions.toast
import ecalle.com.bmybank.fragments.LoadingLoansFragment
import ecalle.com.bmybank.fragments.MyLoansFragment
import ecalle.com.bmybank.fragments.PublicLoansFragment
import ecalle.com.bmybank.realm.bo.Loan
import org.jetbrains.anko.support.v4.ctx

/**
 * Created by Thomas Ecalle on 03/04/2018.
 */
class PendingLoansFragment : LoadingLoansFragment(), LoansAdapter.OnLoanClickListener
{
    override fun onLoanClick(loan: Loan, userFirstName: String, userLastName: String)
    {
        val intent = Intent(ctx, LoanViewerActivity::class.java)
        intent.putExtra(MyLoansFragment.MY_LOAN_KEY, loan)
        intent.putExtra(LoanViewerActivity.USER_FIRSTNAME_KEY, userFirstName)
        intent.putExtra(LoanViewerActivity.USER_LASTNAME_KEY, userLastName)
        startActivity(intent)
    }

    override fun getLoanClickListener(): LoansAdapter.OnLoanClickListener
    {
        return this
    }

    override fun load()
    {
        loadThenGetLoans()
    }

    override fun getTitle(): String
    {
        return getString(R.string.pending_loans_title)
    }

    override fun getLoansType(): String
    {
        return Constants.PENDING_LOANS
    }

}