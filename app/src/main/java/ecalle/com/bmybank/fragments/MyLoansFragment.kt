package ecalle.com.bmybank.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import ecalle.com.bmybank.Constants
import ecalle.com.bmybank.R
import ecalle.com.bmybank.fragments.my_loans.FinishedLoansFragment
import ecalle.com.bmybank.fragments.my_loans.InNegociationLoansFragment
import ecalle.com.bmybank.fragments.my_loans.InProgressLoansFragment
import ecalle.com.bmybank.fragments.my_loans.MyPendingLoansFragment
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.ctx


/**
 * Created by Thomas Ecalle on 03/04/2018.
 */
class MyLoansFragment : Fragment(), View.OnClickListener, AdapterView.OnItemSelectedListener
{

    companion object
    {
        val MY_LOAN_KEY = "myLoanKey"
    }

    private lateinit var spinner: Spinner
    private lateinit var fragmentContainer: FrameLayout


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_my_loans, container, false)

        spinner = view.find<Spinner>(R.id.spinner)
        fragmentContainer = view.find(R.id.fragmentContainer)

        populateSpinner()

        return view
    }

    private fun populateSpinner()
    {
        spinner.onItemSelectedListener = this

        // Spinner Drop down elements
        val states = ArrayList<String>()
        states.add(Constants.PENDING_LOANS)
        states.add(Constants.IN_NEGOCIATION_LOANS)
        states.add(Constants.IN_PROGRESS_LOANS)
        states.add(Constants.FINISHED_LOANS)

        // Creating adapter for spinner
        val dataAdapter = ArrayAdapter<String>(ctx, R.layout.spinner_item, states)

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // attaching data adapter to spinner
        spinner.adapter = dataAdapter
    }

    override fun onClick(p0: View?)
    {

    }

    override fun onNothingSelected(parent: AdapterView<*>?)
    {
        Toast.makeText(parent?.context, "Nothing selected", Toast.LENGTH_LONG).show()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long)
    {
        // On selecting a spinner item
        val item = parent?.getItemAtPosition(position).toString()

        var fragment = when (item)
        {
            Constants.IN_PROGRESS_LOANS -> InProgressLoansFragment()
            Constants.IN_NEGOCIATION_LOANS -> InNegociationLoansFragment()
            Constants.FINISHED_LOANS -> FinishedLoansFragment()
            else ->
            {
                MyPendingLoansFragment()
            }
        } as Fragment


        fragmentManager?.beginTransaction()?.replace(fragmentContainer.id, fragment)?.commit()
    }

}