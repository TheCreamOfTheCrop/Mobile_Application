package ecalle.com.bmybank.fragments.my_loans_pager

import ecalle.com.bmybank.Constants
import ecalle.com.bmybank.R
import ecalle.com.bmybank.fragments.LoadingLoansFragment

/**
 * Created by Thomas Ecalle on 03/04/2018.
 */
class InProgressFragment : LoadingLoansFragment()
{
    override fun load()
    {
        loadThenGetLoans()
    }

    override fun getLoansType(): String
    {
        return Constants.IN_PROGRESS_LOANS
    }

    override fun getTitle(): String
    {
        return getString(R.string.in_progress_loans_title)
    }
}
