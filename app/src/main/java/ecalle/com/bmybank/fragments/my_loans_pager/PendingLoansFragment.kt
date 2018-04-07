package ecalle.com.bmybank.fragments.my_loans_pager


import ecalle.com.bmybank.Constants
import ecalle.com.bmybank.R
import ecalle.com.bmybank.fragments.LoadingLoansFragment

/**
 * Created by Thomas Ecalle on 03/04/2018.
 */
class PendingLoansFragment : LoadingLoansFragment()
{
    override fun getTitle(): String
    {
        return getString(R.string.pending_loans_title)
    }

    override fun getLoansType(): String
    {
        return Constants.PENDING_LOANS
    }

}