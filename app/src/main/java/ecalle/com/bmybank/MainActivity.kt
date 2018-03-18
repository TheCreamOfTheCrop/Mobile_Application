package ecalle.com.bmybank

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.TextView
import ecalle.com.bmybank.fragments.PublicProfileFragment
import ecalle.com.bmybank.fragments.inscription_steps.ProfileModificationFragment
import ecalle.com.bmybank.realm.RealmServices
import ecalle.com.bmybank.realm.bo.User
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.find
import org.jetbrains.anko.startActivity

/**
 * Created by Thomas Ecalle on 18/03/2018.
 */
class MainActivity : AppCompatActivity(), ToolbarManager, NavigationView.OnNavigationItemSelectedListener
{
    override val toolbar by lazy { find<Toolbar>(R.id.toolbar) }

    lateinit private var navigationView: NavigationView
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigationView = find(R.id.navigationView)

        toolbarTitle = getString(R.string.bmyBank)

        val userUid: String? = intent.getStringExtra(Constants.SERIALIZED_USER_UID)

        if (userUid != null)
        {
            user = RealmServices.getCurrentUser(userUid)

            updateHeader(user)
        }

        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener(this)

        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, PublicProfileFragment()).commit()
    }

    override fun onBackPressed()
    {
        if (drawer_layout.isDrawerOpen(GravityCompat.START))
        {
            drawer_layout.closeDrawer(GravityCompat.START)
        }
        else
        {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean
    {
        when (item.itemId)
        {
            R.id.nav_profile ->
            {
                fragmentManager.beginTransaction().replace(R.id.fragmentContainer, PublicProfileFragment()).commit()
            }
            R.id.nav_edit_profile ->
            {
                fragmentManager.beginTransaction().replace(R.id.fragmentContainer, ProfileModificationFragment()).commit()
            }
            R.id.logout ->
            {
                logout()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun logout()
    {
        RealmServices.deleteCurrentUser(user?.uid)
        val sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        sharedPreferences.edit().remove(Constants.USER_UUID_PREFERENCES_KEY).apply()
        startActivity<LoginActivity>()
        finish()
    }

    @SuppressLint("SetTextI18n")
    private fun updateHeader(user: User?)
    {
        if (user != null)
        {
            val headerView = navigationView.getHeaderView(0)
            val headerName = headerView.find<TextView>(R.id.navHeaderName)
            val headerAccountValidity = headerView.find<TextView>(R.id.navHeaderAccountValidity)

            headerName.text = "${user.firstname} ${user.lastname}"
            headerAccountValidity.text = if (user.isAccountValidate) getString(R.string.valid_account_label) else getString(R.string.not_valid_account_label)
        }

    }

}
