package ecalle.com.bmybank.fragments

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ecalle.com.bmybank.AddLoanActivity
import ecalle.com.bmybank.R
import ecalle.com.bmybank.adapters.MyLoansPagerAdapter
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.startActivity

/**
 * Created by Thomas Ecalle on 03/04/2018.
 */
class MyLoansFragment : Fragment(), View.OnClickListener
{

    private lateinit var pagerAdapter: MyLoansPagerAdapter
    private lateinit var viewPager: ViewPager
    private lateinit var addLoanButton: FloatingActionButton
    private lateinit var tabs: TabLayout


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_my_loans, container, false)

        viewPager = view.find<ViewPager>(R.id.viewPager)
        tabs = view.find(R.id.tabs)
        addLoanButton = view.find(R.id.addLoanButton)

        addLoanButton.setOnClickListener(this)
        pagerAdapter = MyLoansPagerAdapter(fragmentManager!!, context)

        viewPager.adapter = pagerAdapter
        tabs.setupWithViewPager(viewPager)

        tabs.getTabAt(0)?.setIcon(R.drawable.ic_pending)
        tabs.getTabAt(1)?.setIcon(R.drawable.ic_negociation)
        tabs.getTabAt(2)?.setIcon(R.drawable.ic_in_progress)
        tabs.getTabAt(3)?.setIcon(R.drawable.ic_check)

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

    override fun onClick(view: View?)
    {
        when (view?.id)
        {
            addLoanButton.id -> startActivity<AddLoanActivity>()
        }
    }

}