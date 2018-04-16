package ecalle.com.bmybank

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import ecalle.com.bmybank.adapters.PagerAdapter
import ecalle.com.bmybank.realm.RealmServices
import ecalle.com.bmybank.realm.bo.User
import org.jetbrains.anko.find


/**
 * Created by Thomas Ecalle on 16/04/2018.
 */
class MainActivity : AppCompatActivity(), View.OnClickListener
{
    private lateinit var pagerAdapter: PagerAdapter
    private lateinit var viewPager: ViewPager
    private lateinit var tabs: TabLayout
    private var currentUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        currentUser = RealmServices.getCurrentUser(this)

        viewPager = find(R.id.viewPager)
        tabs = find(R.id.tabs)

        pagerAdapter = PagerAdapter(supportFragmentManager)

        viewPager.adapter = pagerAdapter
        tabs.setupWithViewPager(viewPager)

        setupIcons()
    }

    private fun setupIcons()
    {
        val cardOne = LayoutInflater.from(this).inflate(R.layout.tab_layout_header_item, null) as CardView
        val imageViewOne = cardOne.find<ImageView>(R.id.icon)
        imageViewOne.setImageResource(R.drawable.ic_chart)
        tabs.getTabAt(0)?.customView = cardOne

        val cardTwo = LayoutInflater.from(this).inflate(R.layout.tab_layout_header_item, null) as CardView
        val imageViewTwo = cardTwo.find<ImageView>(R.id.icon)
        imageViewTwo.setImageResource(R.drawable.ic_list)
        tabs.getTabAt(1)?.customView = cardTwo


        val cardThree = LayoutInflater.from(this).inflate(R.layout.tab_layout_header_item, null) as CardView
        val imageViewThree = cardThree.find<ImageView>(R.id.icon)
        imageViewThree.setImageResource(R.drawable.ic_person)
        tabs.getTabAt(2)?.customView = cardThree

    }

    override fun onClick(p0: View?)
    {

    }

}
