package ecalle.com.bmybank.fragments.my_loans

import android.content.Intent
import ecalle.com.bmybank.Constants
import ecalle.com.bmybank.InProgressLoanViewerActivity
import ecalle.com.bmybank.adapters.LoansAdapter
import ecalle.com.bmybank.fragments.LoadingLoansFragment
import ecalle.com.bmybank.fragments.MyLoansFragment
import ecalle.com.bmybank.realm.bo.Loan
import ecalle.com.bmybank.realm.bo.User
import org.jetbrains.anko.support.v4.ctx

/**
 * Created by Thomas Ecalle on 03/04/2018.
 */
class InProgressLoansFragment : LoadingLoansFragment(), LoansAdapter.OnLoanClickListener
{


    override fun onLoanClick(loan: Loan, user: User?, color: LoansAdapter.Color)
    {
        val intent = Intent(ctx, InProgressLoanViewerActivity::class.java)
        intent.putExtra(MyLoansFragment.LOAN_KEY, loan)
        intent.putExtra(InProgressLoanViewerActivity.USER_KEY, user)
        intent.putExtra(InProgressLoanViewerActivity.COLOR_KEY, color)
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

    override fun getLoansType(): String
    {
        return Constants.IN_PROGRESS_LOANS
    }

}