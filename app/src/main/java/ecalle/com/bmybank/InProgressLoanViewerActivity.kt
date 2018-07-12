package ecalle.com.bmybank

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ShareCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.*
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import ecalle.com.bmybank.adapters.LoansAdapter
import ecalle.com.bmybank.adapters.RefundsAdapter
import ecalle.com.bmybank.custom_components.BMyXValuesFormatter
import ecalle.com.bmybank.firebase.GlideApp
import ecalle.com.bmybank.fragments.MyLoansFragment
import ecalle.com.bmybank.realm.RealmServices
import ecalle.com.bmybank.realm.bo.Loan
import ecalle.com.bmybank.realm.bo.Refund
import ecalle.com.bmybank.realm.bo.User
import ecalle.com.bmybank.services.BmyBankApi
import ecalle.com.bmybank.services_responses_bo.NoteListReponse
import ecalle.com.bmybank.services_responses_bo.RefundsResponse
import ecalle.com.bmybank.services_responses_bo.UserResponse
import org.jetbrains.anko.ctx
import org.jetbrains.anko.find
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Thomas Ecalle on 10/04/2018.
 */
class InProgressLoanViewerActivity : AppCompatActivity(), ToolbarManager, View.OnClickListener
{


    companion object
    {
        val USER_KEY = "userKey"
        val COLOR_KEY = "colorKey"
    }

    override val toolbar by lazy { find<Toolbar>(R.id.toolbar) }

    private lateinit var description: TextView
    private lateinit var amount: TextView
    private lateinit var delay: TextView
    private lateinit var rate: TextView
    private lateinit var firstName: TextView
    private lateinit var lastName: TextView
    private lateinit var waveHeader: RelativeLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var userInformations: LinearLayout
    private lateinit var avatar: CircleImageView
    private lateinit var refundsLayout: LinearLayout
    private lateinit var repay: Button

    private lateinit var refundsRecyclerView: RecyclerView
    private lateinit var refundsAdapter: RefundsAdapter
    private lateinit var refundsList: MutableList<Refund>
    private lateinit var refundsLoader: ProgressBar

    private lateinit var loan: Loan
    private var currentUser: User? = null
    private var otherUser: User? = null
    private var color: LoansAdapter.Color? = LoansAdapter.Color.BLUE

    private lateinit var noteContainer: LinearLayout
    private lateinit var noteLabel: TextView
    private lateinit var noteButton: TextView
    private lateinit var contactButton: TextView

    private lateinit var lineChart: LineChart
    private lateinit var lineData: LineData
    private lateinit var acceptationDateCalendar: Calendar

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_in_progress_loan_viewer)

        description = find(R.id.description)
        amount = find(R.id.amount)
        rate = find(R.id.rate)
        delay = find(R.id.delay)
        firstName = find(R.id.firstName)
        lastName = find(R.id.lastName)
        waveHeader = find(R.id.waveHeader)
        progressBar = find(R.id.progressBar)
        userInformations = find(R.id.userInformations)
        avatar = find(R.id.avatar)
        refundsLayout = find(R.id.refundsLayout)
        refundsRecyclerView = find(R.id.recyclerView)
        refundsLoader = find(R.id.refundsLoader)
        repay = find(R.id.repay)
        lineChart = find(R.id.lineChart)

        noteContainer = find(R.id.noteContainer)
        noteLabel = find(R.id.noteLabel)
        noteButton = find(R.id.noteButton)
        contactButton = find(R.id.contactButton)

        toolbarTitle = getString(R.string.loan_viewver_toolbar_title)
        enableHomeAsUp { onBackPressed() }

        if (intent == null)
        {
            finish()
        }

        loan = intent.getSerializableExtra(MyLoansFragment.LOAN_KEY) as Loan
        currentUser = RealmServices.getCurrentUser(this)
        getRefundsThenShow()

        if (loan.user_requester_id == currentUser?.id)
        {
            repay.visibility = View.VISIBLE
        }

        startFillingUpChart()

        findUserInformations()


        color = intent.getSerializableExtra(InProgressLoanViewerActivity.COLOR_KEY) as LoansAdapter.Color?

        if (color == null)
        {
            color = LoansAdapter.Color.BLUE
        }

        var drawableId = if (color == LoansAdapter.Color.BLUE) R.drawable.vague else R.drawable.orange_wave

        if (loan.isLate())
        {
            color = LoansAdapter.Color.RED
            drawableId = R.drawable.red_wave

            if (currentUser?.id != loan.user_requester_id)
            {
                noteContainer.visibility = View.VISIBLE
                handleNotation()
            }

        }
        changeColor(color!!, this)

        waveHeader.background = ContextCompat.getDrawable(this, drawableId)

        fillInformations()
        repay.setOnClickListener(this)
        avatar.setOnClickListener(this)
        noteButton.setOnClickListener(this)
        contactButton.setOnClickListener(this)
        firstName.text = otherUser?.firstname
        lastName.text = otherUser?.lastname

    }

    private fun startFillingUpChart()
    {
        val xAxis = mutableListOf<Int>()
        val amountWithRate = (loan.amount * (1 + loan.rate / 100))

        val acceptationDateTimestamp = loan.acceptationDate?.toLongOrNull() ?: return
        acceptationDateCalendar = Calendar.getInstance()
        acceptationDateCalendar.time = Date(acceptationDateTimestamp)


        if (acceptationDateCalendar.get(Calendar.MONTH) == 0)
        {
            xAxis.add(11)
        }
        else
        {
            xAxis.add(acceptationDateCalendar.get(Calendar.MONTH).minus(1) % 12)
        }

        for (i in 0..loan.delay)
        {
            xAxis.add(acceptationDateCalendar.get(Calendar.MONTH).plus(i) % 12)
        }

        lineChart.xAxis.axisMinimum = 0f
        lineChart.xAxis.axisMaximum = loan.delay.toFloat()
        lineChart.xAxis.granularity = 1f
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        lineChart.xAxis.valueFormatter = BMyXValuesFormatter(xAxis)
        //lineChart.xAxis.setDrawAxisLine(false)

        lineChart.axisRight.isEnabled = false

        lineChart.axisLeft.setDrawAxisLine(false)

        //lineChart.axisLeft.setDrawLabels(false)
        lineChart.axisLeft.axisMinimum = 0f
        lineChart.axisLeft.axisMaximum = amountWithRate
        lineChart.axisLeft.granularity = 10f

        lineChart.description = null

        lineChart.axisLeft.setDrawGridLines(false)
        lineChart.xAxis.setDrawGridLines(false)

        lineChart.setVisibleXRange(1f, 3f)
        lineChart.setVisibleYRange(1f, amountWithRate / loan.delay * 4, lineChart.axisLeft.axisDependency)

        val entries = ArrayList<Entry>()

        for (i in 0..loan.delay)
        {
            entries.add(Entry((i + 1).toFloat(), amountWithRate / loan.delay))
        }

        val lineDataSet = LineDataSet(entries, "Remboursement souhaité")

        lineDataSet.color = ContextCompat.getColor(ctx, R.color.colorAccent)
        lineDataSet.valueTextSize = 20f
        lineDataSet.valueTextColor = ContextCompat.getColor(ctx, R.color.colorAccent)
        lineDataSet.lineWidth = 3f
        lineDataSet.setDrawValues(false)

        lineData = LineData(lineDataSet)

        lineChart.data = lineData
        lineChart.isLogEnabled = true
        lineChart.setDrawBorders(false)
        lineChart.legend.form = Legend.LegendForm.CIRCLE

        lineChart.invalidate()
    }

    private fun handleNotation()
    {
        val api = BmyBankApi.getInstance(ctx)
        val loanNotesRequest = api.getLoanNotes(loan.id)

        loanNotesRequest.enqueue(object : Callback<NoteListReponse>
        {


            override fun onResponse(call: Call<NoteListReponse>?, response: Response<NoteListReponse>?)
            {
                val res = response?.body()
                if (res?.success != null && res.success)
                {
                    if (res.note.isEmpty())
                    {
                        noteButton.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<NoteListReponse>?, t: Throwable?)
            {
                Log.i("thomas", "Error on getting notes from API")
            }
        })


    }

    private fun getRefundsThenShow()
    {
        val api = BmyBankApi.getInstance(ctx)
        val findUserByIdRequest = api.getRefunds(loan.id)

        findUserByIdRequest.enqueue(object : Callback<RefundsResponse>
        {
            override fun onResponse(call: Call<RefundsResponse>, response: Response<RefundsResponse>)
            {
                val res = response.body()
                if (res?.success != null && res?.success)
                {
                    refundsList = res.refunds
                    fillChartWithRefunds(refundsList)
                    refundsAdapter = RefundsAdapter(refundsList)

                    refundsRecyclerView.layoutManager = LinearLayoutManager(ctx)
                    refundsRecyclerView.adapter = refundsAdapter

                    refundsLoader.visibility = View.GONE
                    refundsRecyclerView.visibility = View.VISIBLE
                }
            }


            override fun onFailure(call: Call<RefundsResponse>, t: Throwable)
            {
                toast(R.string.not_internet)
            }
        })

    }

    private fun fillChartWithRefunds(refunds: List<Refund>)
    {

        loan.acceptationDate?.toLongOrNull() ?: return
        if (refunds.isEmpty()) return

        val entries = ArrayList<Entry>()

        var lastEntry = Entry()
        var lastIndex = -1f

        refunds.forEachIndexed { index, refund ->
            run {

                val monthIndex = getRefundMonthIndex(refund.creationdate)
                if (monthIndex == lastIndex)
                {
                    lastEntry.y = lastEntry.y + refund.amount.toFloat()
                }
                else
                {
                    lastIndex = monthIndex
                    lastEntry = Entry(monthIndex, refund.amount.toFloat())
                }


                entries.add(lastEntry)
            }
        }

        val lineDataSet = LineDataSet(entries, "Remboursement réel")

        lineDataSet.color = ContextCompat.getColor(ctx, R.color.colorPrimary)
        lineDataSet.valueTextSize = 20f
        lineDataSet.valueTextColor = ContextCompat.getColor(ctx, R.color.colorPrimary)
        lineDataSet.lineWidth = 3f

        lineData.addDataSet(lineDataSet)

        lineData.notifyDataChanged()
        lineChart.notifyDataSetChanged()
        lineChart.invalidate()
    }

    private fun getRefundMonthIndex(stringTimeStamp: String): Float
    {
        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.FRANCE).parse(stringTimeStamp)

        val calendar = Calendar.getInstance()
        calendar.time = date

        return (calendar.get(Calendar.MONTH) - acceptationDateCalendar.get(Calendar.MONTH) + 1).toFloat()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        if (requestCode == FinishedLoanViewerActivity.ADD_NOTE_REQUEST_CODE)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                noteButton.visibility = View.GONE
                toast(R.string.note_added_with_success)
            }
        }
    }

    override fun onClick(view: View?)
    {
        when (view?.id)
        {
            repay.id ->
            {
                val intent = Intent(ctx, PaymentActivity::class.java)
                intent.action = PaymentActivity.REFUND_ACTION
                intent.putExtra(PaymentActivity.LOAN_KEY, loan)
                intent.putExtra(PaymentActivity.OTHER_USER_KEY, otherUser)
                startActivity(intent)
            }
            avatar.id ->
            {
                val intent = Intent(PendingLoanViewerActivity@ this, ProfileViewerActivity::class.java)
                intent.putExtra(ProfileViewerActivity.USER_ID_KEY, loan.user_requester_id)
                val firstName = if (loan.user_requester_id == currentUser?.id) currentUser?.firstname else otherUser?.firstname
                intent.putExtra(ProfileViewerActivity.USER_FIRSTNAME_KEY, firstName)
                intent.putExtra(ProfileViewerActivity.COLOR_KEY, color)

                startActivity(intent)
            }
            noteButton.id ->
            {
                val intent = Intent(FinishedLoanViewerActivity@ this, AddNoteActivity::class.java)
                intent.putExtra(AddNoteActivity.LOAN_ID_KEY, loan.id)
                intent.putExtra(AddNoteActivity.USER_ID_KEY, loan.user_requester_id)

                startActivityForResult(intent, FinishedLoanViewerActivity.ADD_NOTE_REQUEST_CODE)
            }
            contactButton.id ->
            {
                val shareBuilder = ShareCompat.IntentBuilder.from(this)
                shareBuilder.setType("message/rfc822")
                shareBuilder.addEmailTo(Constants.ADMIN_EMAIL_ADRESS)
                shareBuilder.setSubject(getString(R.string.loan_delay_alert_email_subject))

                val pendingIntent = shareBuilder.intent

                if (pendingIntent.resolveActivity(getPackageManager()) != null)
                {
                    startActivity(pendingIntent)
                }
            }
        }
    }

    override fun onBackPressed()
    {
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun fillInformations()
    {
        description.text = loan.description
        amount.text = loan.amount.toString()
        rate.text = loan.rate.toString()
        delay.text = loan.delay.toString()
    }

    private fun getRequesterInformations()
    {
        val api = BmyBankApi.getInstance(ctx)
        loan.user_provider_id?.let {
            val findUserByIdRequest = api.findUserById(loan.user_requester_id!!)


            findUserByIdRequest.enqueue(object : Callback<UserResponse>
            {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>)
                {
                    val userResponse = response.body()
                    if (userResponse?.success != null && userResponse?.success)
                    {
                        firstName.text = userResponse.user.firstname
                        lastName.text = userResponse.user.lastname

                        removeLoaderOnNames(userResponse.user)
                    }
                }


                override fun onFailure(call: Call<UserResponse>, t: Throwable)
                {
                    toast(R.string.not_internet)
                }
            })
        }
    }

    private fun getProviderInformations()
    {

        val api = BmyBankApi.getInstance(ctx)
        loan.user_provider_id?.let {
            val findUserByIdRequest = api.findUserById(loan.user_provider_id!!)


            findUserByIdRequest.enqueue(object : Callback<UserResponse>
            {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>)
                {
                    val userResponse = response.body()
                    if (userResponse?.success != null && userResponse?.success)
                    {
                        otherUser = userResponse.user
                    }
                }


                override fun onFailure(call: Call<UserResponse>, t: Throwable)
                {
                    toast(R.string.not_internet)
                }
            })
        }
    }

    private fun findUserInformations()
    {
        getProviderInformations()
        getRequesterInformations()

    }

    private fun removeLoaderOnNames(user: User)
    {
        progressBar.visibility = View.GONE
        userInformations.visibility = View.VISIBLE

        var emailWithoutSpecialCharacters = user?.email?.replace("@", "")
        emailWithoutSpecialCharacters = emailWithoutSpecialCharacters?.replace(".", "")
        emailWithoutSpecialCharacters = emailWithoutSpecialCharacters.plus(".jpg")


        if (emailWithoutSpecialCharacters != null)
        {
            val firebaseStorage = FirebaseStorage.getInstance()
            val reference = firebaseStorage.reference.child("${Constants.PROFILE_PICTURES_NODE}/$emailWithoutSpecialCharacters")

            // Load the image using Glide
            GlideApp.with(this)
                    .load(reference)
                    .placeholder(R.drawable.default_profile)
                    .error(R.drawable.default_profile)
                    .into(avatar)

        }

    }

}