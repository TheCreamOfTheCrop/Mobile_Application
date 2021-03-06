package ecalle.com.bmybank

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.google.firebase.iid.FirebaseInstanceId
import ecalle.com.bmybank.adapters.PagerAdapter
import ecalle.com.bmybank.extensions.log
import ecalle.com.bmybank.firebase.Utils
import ecalle.com.bmybank.fragments.LoadingLoansFragment
import ecalle.com.bmybank.fragments.PublicLoansFragment
import ecalle.com.bmybank.fragments.my_loans.MyPendingLoansFragment
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

        initFCM()

        currentUser = RealmServices.getCurrentUser(this)

        viewPager = find(R.id.viewPager)
        tabs = find(R.id.tabs)

        pagerAdapter = PagerAdapter(supportFragmentManager)

        viewPager.adapter = pagerAdapter
        tabs.setupWithViewPager(viewPager)

        setupIcons()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        when (requestCode)
        {
            MyPendingLoansFragment.REQUEST_CODE ->
            {
                when (resultCode)
                {
                    Activity.RESULT_OK ->
                    {
                        LocalBroadcastManager.getInstance(this).sendBroadcast(Intent(LoadingLoansFragment.RELOAD_ACTION))
                    }
                }
            }

            PublicLoansFragment.REQUEST_CODE ->
            {

            }
        }
    }

    private fun sendRegistrationToServer(token: String?)
    {
        log("sendRegistrationToServer: sending token to server: " + token!!)
        val reference = Utils.getDatabase().reference
        reference.child("users")
                .child(RealmServices.getCurrentUser(this)?.id.toString())
                .child("messaging_token")
                .setValue(token)
    }


    private fun initFCM()
    {
        val token = FirebaseInstanceId.getInstance().token
        log("initFCM: token: " + token!!)
        sendRegistrationToServer(token)

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
        imageViewThree.setImageResource(R.drawable.ic_chat)
        imageViewThree.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY)
        tabs.getTabAt(2)?.customView = cardThree

        val cardFour = LayoutInflater.from(this).inflate(R.layout.tab_layout_header_item, null) as CardView
        val imageViewFour = cardFour.find<ImageView>(R.id.icon)
        imageViewFour.setImageResource(R.drawable.ic_person)
        tabs.getTabAt(3)?.customView = cardFour

    }

    override fun onClick(p0: View?)
    {

    }

}
