package ecalle.com.bmybank.adapters

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import ecalle.com.bmybank.fragments.my_loans_pager.FinishedLoansFragment
import ecalle.com.bmybank.fragments.my_loans_pager.InNegociationLoansFragment
import ecalle.com.bmybank.fragments.my_loans_pager.InProgressFragment
import ecalle.com.bmybank.fragments.my_loans_pager.PendingLoansFragment

/**
 * Created by Thomas Ecalle on 03/04/2018.
 */
class MyLoansPagerAdapter(fm: FragmentManager, private val context: Context?) : FragmentPagerAdapter(fm)
{
    private val framgents: ArrayList<Fragment> = ArrayList()
    private var currentPosition: Int = 0

    init
    {
        framgents.add(PendingLoansFragment())
        framgents.add(InNegociationLoansFragment())
        framgents.add(InProgressFragment())
        framgents.add(FinishedLoansFragment())


    }

    override fun getItem(position: Int): Fragment
    {
        this.currentPosition = position
        return framgents[position]
    }

    override fun getPageTitle(position: Int): CharSequence?
    {
        /*
        val fragment = framgents.get(position) as MyLoansPagerFragment
        return context?.getString(fragment.getTitle())
         */
        return null

    }

    override fun getCount(): Int
    {
        return 4
    }

    fun getCurrentPosition(): Int
    {
        return currentPosition
    }

}