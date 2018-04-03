package ecalle.com.bmybank.fragments

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ecalle.com.bmybank.R
import ecalle.com.bmybank.adapters.MyLoansPagerAdapter
import org.jetbrains.anko.find

/**
 * Created by Thomas Ecalle on 03/04/2018.
 */
class MyLoansFragment : Fragment()
{
    private lateinit var pagerAdapter: MyLoansPagerAdapter
    private lateinit var viewPager: ViewPager
    private lateinit var tabs: TabLayout


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_my_loans, container, false)

        viewPager = view.find(R.id.viewPager)
        tabs = view.find(R.id.tabs)

        pagerAdapter = MyLoansPagerAdapter(fragmentManager!!, context)

        viewPager.adapter = pagerAdapter
        tabs.setupWithViewPager(viewPager)
        return view
    }

    fun getCurrentPosition(): Int
    {
        return viewPager.currentItem
    }

    fun handleBack()
    {
        viewPager.currentItem = getCurrentPosition() - 1
    }

}