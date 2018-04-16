package ecalle.com.bmybank

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import ecalle.com.bmybank.adapters.LoansAdapter
import ecalle.com.bmybank.custom_components.BeMyDialog
import ecalle.com.bmybank.fragments.MyLoansFragment
import ecalle.com.bmybank.fragments.my_loans_pager.PublicLoansFragment
import ecalle.com.bmybank.realm.bo.Loan
import ecalle.com.bmybank.services.BmyBankApi
import org.jetbrains.anko.find
import org.jetbrains.anko.toast

/**
 * Created by Thomas Ecalle on 10/04/2018.
 */
class LoanViewerActivity : AppCompatActivity(), ToolbarManager, View.OnClickListener
{


    companion object
    {
        val USER_FIRSTNAME_KEY = "userFirstNameKey"
        val USER_LASTNAME_KEY = "userFirstNameKey"
        val COLOR_KEY = "colorKey"
    }

    override val toolbar by lazy { find<Toolbar>(R.id.toolbar) }


    private lateinit var loadingDialog: BeMyDialog
    private lateinit var description: TextView
    private lateinit var amount: TextView
    private lateinit var delay: TextView
    private lateinit var rate: TextView
    private lateinit var firstName: TextView
    private lateinit var lastName: TextView
    private lateinit var accept: Button
    private lateinit var negociate: Button
    private lateinit var waveHeader: RelativeLayout

    private lateinit var loan: Loan
    private lateinit var userFirstName: String
    private lateinit var userLastName: String
    private var color: LoansAdapter.Color? = LoansAdapter.Color.BLUE

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loan_viewer)

        description = find(R.id.description)
        amount = find(R.id.amount)
        rate = find(R.id.rate)
        delay = find(R.id.delay)
        firstName = find(R.id.firstName)
        lastName = find(R.id.lastName)
        accept = find(R.id.accept)
        negociate = find(R.id.negociate)
        waveHeader = find(R.id.waveHeader)

        toolbarTitle = getString(R.string.loan_viewver_toolbar_title)
        enableHomeAsUp { onBackPressed() }

        if (intent == null)
        {
            finish()
        }

        if (intent.hasExtra(PublicLoansFragment.PUBLIC_LOAN_KEY))
        {
            loan = intent.getSerializableExtra(PublicLoansFragment.PUBLIC_LOAN_KEY) as Loan
            userFirstName = intent.getStringExtra(USER_FIRSTNAME_KEY)
            userLastName = intent.getStringExtra(USER_LASTNAME_KEY)
            accept.visibility = View.VISIBLE
            negociate.visibility = View.VISIBLE
        }
        else if (intent.hasExtra(MyLoansFragment.MY_LOAN_KEY))
        {
            loan = intent.getSerializableExtra(MyLoansFragment.MY_LOAN_KEY) as Loan
            userFirstName = intent.getStringExtra(USER_FIRSTNAME_KEY)
            userLastName = intent.getStringExtra(USER_LASTNAME_KEY)
            accept.visibility = View.GONE
            negociate.visibility = View.GONE
        }

        color = intent.getSerializableExtra(LoanViewerActivity.COLOR_KEY) as LoansAdapter.Color?

        if (color == null)
        {
            color = LoansAdapter.Color.BLUE
        }

        changeColor(color!!, this)
        val drawable = if (color == LoansAdapter.Color.BLUE) R.drawable.vague else R.drawable.orange_wave

        waveHeader.background = ContextCompat.getDrawable(this, drawable)

        description.text = loan.description
        amount.text = loan.amount.toString()
        rate.text = loan.rate.toString()
        delay.text = loan.delay.toString()
        accept.setOnClickListener(this)
        negociate.setOnClickListener(this)
        firstName.text = userFirstName
        lastName.text = userLastName

    }

    override fun onClick(view: View?)
    {
        when (view?.id)
        {
            accept.id -> toast("clicked on accept")
            negociate.id -> negociate()
        }
    }

    private fun negociate()
    {
        //loadingDialog = customAlert(message = R.string.negociating_loading, type = BeMyDialog.TYPE.LOADING)
        val api = BmyBankApi.getInstance(this)
        val loginRequest = api.addNegociation(loan.id, loan.amount, loan.rate, loan.delay)


    }


}