package ecalle.com.bmybank.fragments.my_loans_pager

import ecalle.com.bmybank.Constants
import ecalle.com.bmybank.R
import ecalle.com.bmybank.fragments.LoadingLoansFragment

/**
 * Created by Thomas Ecalle on 03/04/2018.
 */
class InNegociationLoansFragment : LoadingLoansFragment()
{
    override fun getLoansType(): String
    {
        return Constants.IN_NEGOCIATION_LOANS
    }

    override fun getTitle(): String
    {
        return getString(R.string.in_negociation_loans_title)
    }
}