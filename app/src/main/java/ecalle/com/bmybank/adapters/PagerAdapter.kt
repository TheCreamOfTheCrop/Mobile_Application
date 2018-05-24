package ecalle.com.bmybank.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import ecalle.com.bmybank.fragments.ChannelsFragment
import ecalle.com.bmybank.fragments.MyLoansFragment
import ecalle.com.bmybank.fragments.ProfileScreenFragment
import ecalle.com.bmybank.fragments.PublicLoansFragment

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
            2 -> ChannelsFragment()
            else -> ProfileScreenFragment()
        }
    }

    override fun getCount(): Int
    {
        return 4
    }

}