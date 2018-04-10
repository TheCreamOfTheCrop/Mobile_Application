package ecalle.com.bmybank.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import ecalle.com.bmybank.LoanViewerActivity
import ecalle.com.bmybank.R
import ecalle.com.bmybank.adapters.LoansAdapter
import ecalle.com.bmybank.bo.GettingUserLoansResponse
import ecalle.com.bmybank.realm.bo.Loan
import ecalle.com.bmybank.services.BmyBankApi
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.onRefresh
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Thomas Ecalle on 07/04/2018.
 */
class PublicLoansFragment : Fragment(), View.OnClickListener, LoansAdapter.OnLoanClickListener
{


    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var loader: ProgressBar
    private lateinit var errorView: LinearLayout
    private lateinit var errorText: TextView
    private lateinit var retry: Button
    private lateinit var loans: List<Loan>

    companion object
    {
        val PUBLIC_LOAN_KEY = "publicLoanKey"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_public_loans, container, false)

        recyclerView = view.find<RecyclerView>(R.id.recyclerView)
        swipeRefreshLayout = view.find(R.id.swipeRefreshLayout)
        loader = view.find(R.id.loader)
        errorView = view.find(R.id.errorView)
        errorText = view.find(R.id.errorText)
        retry = view.find(R.id.retry)


        swipeRefreshLayout.onRefresh {
            getLoans()
            swipeRefreshLayout.isRefreshing = false
        }

        retry.setOnClickListener(this)

        loadThenGetLoans()

        return view
    }

    protected fun loadThenGetLoans()
    {
        loader.visibility = View.VISIBLE
        errorView.visibility = View.GONE
        recyclerView.visibility = View.GONE

        getLoans()
    }

    override fun onClick(view: View?)
    {
        when (view?.id)
        {
            retry.id ->
            {
                loadThenGetLoans()
            }
        }
    }

    private fun setupList()
    {
        recyclerView.layoutManager = LinearLayoutManager(ctx)
        recyclerView.adapter = LoansAdapter(loans, this)

        recyclerView.visibility = View.VISIBLE

    }

    private fun getLoans()
    {

        val api = BmyBankApi.getInstance(ctx)
        val personalLoansRequest = api.findPublicLoans()

        personalLoansRequest.enqueue(object : Callback<GettingUserLoansResponse>
        {
            override fun onResponse(call: Call<GettingUserLoansResponse>, response: Response<GettingUserLoansResponse>)
            {
                if (response.code() == 400)
                {
                    showInfo()
                }
                else
                {
                    loader.visibility = View.GONE
                    val loansResponse = response.body()
                    if (loansResponse?.success != null && loansResponse?.success)
                    {
                        if (!loansResponse.loans.isEmpty())
                        {
                            loans = loansResponse.loans
                            setupList()
                        }
                        else
                        {
                            showInfo()
                        }

                    }
                }


            }

            override fun onFailure(call: Call<GettingUserLoansResponse>, t: Throwable)
            {
                showInfo(message = getString(R.string.not_internet))
            }
        })
    }

    private fun showInfo(message: String = getString(R.string.no_results))
    {
        loader.visibility = View.GONE
        errorView.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        errorText.text = message
    }

    override fun onLoanClick(loan: Loan, userFirstName: String, userLastName: String)
    {
        val intent = Intent(ctx, LoanViewerActivity::class.java)
        intent.putExtra(PublicLoansFragment.PUBLIC_LOAN_KEY, loan)
        intent.putExtra(LoanViewerActivity.USER_FIRSTNAME_KEY, userFirstName)
        intent.putExtra(LoanViewerActivity.USER_LASTNAME_KEY, userLastName)
        startActivity(intent)
    }
}
