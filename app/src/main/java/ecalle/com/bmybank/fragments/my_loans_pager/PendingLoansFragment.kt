package ecalle.com.bmybank.fragments.my_loans_pager


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ecalle.com.bmybank.R
import ecalle.com.bmybank.interfaces.MyLoansPagerFragment

/**
 * Created by Thomas Ecalle on 03/04/2018.
 */
class PendingLoansFragment : Fragment(), MyLoansPagerFragment
{
    override fun getTitle(): Int
    {
        return R.string.pending_loans_title
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_pending_loans, container, false)


        return view
    }

}