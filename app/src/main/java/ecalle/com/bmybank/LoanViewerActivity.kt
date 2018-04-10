package ecalle.com.bmybank

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.Button
import android.widget.TextView
import ecalle.com.bmybank.fragments.PublicLoansFragment
import ecalle.com.bmybank.realm.bo.Loan
import org.jetbrains.anko.find

/**
 * Created by Thomas Ecalle on 10/04/2018.
 */
class LoanViewerActivity : AppCompatActivity(), ToolbarManager
{

    companion object
    {
        val USER_FIRSTNAME_KEY = "userFirstNameKey"
        val USER_LASTNAME_KEY = "userFirstNameKey"
    }

    override val toolbar by lazy { find<Toolbar>(R.id.toolbar) }

    private var isOtherUserLoan: Boolean = true
    private lateinit var loan: Loan
    private lateinit var userFirstName: String
    private lateinit var userLastName: String

    private lateinit var description: TextView
    private lateinit var amount: TextView
    private lateinit var delay: TextView
    private lateinit var rate: TextView
    private lateinit var firstName: TextView
    private lateinit var lastName: TextView
    private lateinit var accept: Button
    private lateinit var negociate: Button

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

        toolbarTitle = getString(R.string.loan_viewver_toolbar_title)
        enableHomeAsUp { onBackPressed() }

        if (intent == null)
        {
            finish()
        }

        if (intent.hasExtra(PublicLoansFragment.PUBLIC_LOAN_KEY))
        {
            isOtherUserLoan = true
            loan = intent.getSerializableExtra(PublicLoansFragment.PUBLIC_LOAN_KEY) as Loan
            userFirstName = intent.getStringExtra(USER_FIRSTNAME_KEY)
            userLastName = intent.getStringExtra(USER_LASTNAME_KEY)
        }


        description.text = loan.description
        amount.text = loan.amount.toString()
        rate.text = loan.rate.toString()
        delay.text = loan.delay.toString()

        firstName.text = userFirstName
        lastName.text = userLastName

    }
}