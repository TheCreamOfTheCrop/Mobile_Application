package ecalle.com.bmybank.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import ecalle.com.bmybank.fragments.ProfileScreenFragment
import ecalle.com.bmybank.fragments.PublicLoansFragment
import ecalle.com.bmybank.fragments.MyLoansFragment

/**
 * Created by Thomas Ecalle on 03/04/2018.
 */
class PagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm)
{
    private var currentPosition: Int = 0

    override fun getItem(position: Int): Fragment
    {
        this.currentPosition = position

        return when (position)
        {
            0 -> MyLoansFragment()
            1 -> PublicLoansFragment()
            else -> ProfileScreenFragment()
        }
    }

    override fun getCount(): Int
    {
        return 3
    }

    fun reload()
    {

    }

}