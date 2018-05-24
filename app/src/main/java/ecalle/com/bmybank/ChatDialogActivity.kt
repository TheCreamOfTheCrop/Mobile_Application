package ecalle.com.bmybank

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.squareup.moshi.Moshi
import de.hdodenhof.circleimageview.CircleImageView
import ecalle.com.bmybank.extensions.changeStatusBar
import ecalle.com.bmybank.extensions.customAlert
import ecalle.com.bmybank.extensions.log
import ecalle.com.bmybank.firebase.Utils
import ecalle.com.bmybank.firebase.bo.Channel
import ecalle.com.bmybank.firebase.bo.Message
import ecalle.com.bmybank.realm.RealmServices
import ecalle.com.bmybank.realm.bo.Loan
import ecalle.com.bmybank.realm.bo.User
import ecalle.com.bmybank.services.BmyBankApi
import ecalle.com.bmybank.services_respnses_bo.AddingLoanResponse
import ecalle.com.bmybank.services_respnses_bo.SImpleResponse
import ecalle.com.bmybank.services_respnses_bo.UserResponse
import ecalle.com.bmybank.view_holders.MessageViewHolder
import org.jetbrains.anko.ctx
import org.jetbrains.anko.find
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Thomas Ecalle on 20/05/2018.
 */
class ChatDialogActivity : AppCompatActivity(), ToolbarManager
{

    override val toolbar by lazy { find<Toolbar>(R.id.toolbar) }
    private var otherUser: User? = null
    private var currentUser: User? = null
    private lateinit var channel: Channel

    private lateinit var recyclerView: RecyclerView
    private lateinit var send: Button
    private lateinit var messageEditText: EditText
    private var adapter: FirebaseRecyclerAdapter<Message, MessageViewHolder>? = null

    private val REQUIRED = "Required"

    private var mMessageReference: DatabaseReference? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private lateinit var query: Query
    private lateinit var options: FirebaseRecyclerOptions<Message>
    private lateinit var loanLoader: ProgressBar

    private lateinit var negociatedLoanLayout: LinearLayout
    private lateinit var amount: TextView
    private lateinit var rate: TextView
    private lateinit var delay: TextView
    private lateinit var modifyAndSendLoan: Button
    private lateinit var toolbarOtherUserFirstName: TextView
    private lateinit var toolbarOtherUserImage: CircleImageView

    private var loan: Loan? = null

    companion object
    {
        val DISCUSSION_KEY = "discussionKey"
        val OTHER_USER_KEY = "otherUserKey"
        val REQUEST_CODE = 22

    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_dialog)

        changeStatusBar(R.color.colorPrimary, this)

        channel = intent.getSerializableExtra(DISCUSSION_KEY) as Channel
        otherUser = intent.getSerializableExtra(OTHER_USER_KEY) as User?



        currentUser = RealmServices.getCurrentUser(ctx)

        mMessageReference = Utils.getDatabase().getReferenceFromUrl("https://bmybank-2146c.firebaseio.com/listMessages/${channel.list_messages_id}")

        recyclerView = find(R.id.recyclerView)
        send = find(R.id.send)
        messageEditText = find(R.id.messageEditText)
        negociatedLoanLayout = find(R.id.negociatedLoanLayout)
        amount = find(R.id.amount)
        rate = find(R.id.rate)
        delay = find(R.id.delay)
        loanLoader = find(R.id.loanLoader)
        modifyAndSendLoan = find(R.id.modifyAndSendLoan)
        toolbarOtherUserFirstName = find(R.id.toolbarOtherUserFirstName)
        toolbarOtherUserImage = find(R.id.toolbarOtherUserImage)

        if (channel.id_loan != null)
        {
            loanLoader.visibility = View.VISIBLE
            generateLoanUi()
        }

        //firebaseListenerInit()

        send.setOnClickListener {
            submitMessage()
            messageEditText.setText("")
        }

        enableHomeAsUp { onBackPressed() }


        if (otherUser !== null)
        {
            toolbarTitle = "${otherUser?.firstname} ${otherUser?.lastname}"
            setUp()

        }
        else
        {
            loadUserThenDisplayTitle()
        }

        recyclerView.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        if (requestCode == ChatDialogActivity.REQUEST_CODE)
        {
            when (resultCode)
            {
                Activity.RESULT_OK -> customAlert(message = R.string.negociation_well_sent, loop = false)
            }
        }
    }

    private fun generateLoanUi()
    {
        if (channel.id_loan != null)
        {
            val api = BmyBankApi.getInstance(this)
            val findUserByIdRequest = api.findLoanById(channel.id_loan!!)

            findUserByIdRequest.enqueue(object : Callback<AddingLoanResponse>
            {
                override fun onResponse(call: Call<AddingLoanResponse>, response: Response<AddingLoanResponse>)
                {
                    val loanResponse = response.body()
                    if (loanResponse?.success != null && loanResponse?.success)
                    {
                        loan = loanResponse.loan
                        if (loan != null)
                        {
                            amount.text = loan?.amount.toString()
                            rate.text = loan?.rate.toString()
                            delay.text = getString(R.string.repayment_loan_item_label, loan?.delay)
                            loanLoader.visibility = View.GONE

                            if (loan?.user_requester_id == currentUser?.id)
                            {
                                modifyAndSendLoan.visibility = View.VISIBLE
                                modifyAndSendLoan.setOnClickListener {
                                    val intent = Intent(this@ChatDialogActivity, AddLoanActivity::class.java)
                                    intent.putExtra(AddLoanActivity.IS_MODIFYYING_MODE_KEY, true)
                                    intent.putExtra(AddLoanActivity.MODIFYING_LOAN_KEY, loan)
                                    intent.putExtra(AddLoanActivity.MODIFYING_LOAN_OTHER_USER_KEY, otherUser)
                                    startActivityForResult(intent, REQUEST_CODE)
                                }
                            }

                            negociatedLoanLayout.visibility = View.VISIBLE

                            scrollToBottom()
                        }
                    }
                }


                override fun onFailure(call: Call<AddingLoanResponse>, t: Throwable)
                {
                    ecalle.com.bmybank.extensions.log("enable to find loan with id ${channel.id_loan}")
                }
            })
        }

    }

    private fun setUp()
    {

        toolbarOtherUserFirstName.text = otherUser?.firstname
        //toolbarOtherUserImage.src = otherUser?.firstname

        query = mMessageReference?.orderByKey()!!

        options = FirebaseRecyclerOptions.Builder<Message>().setQuery(query, Message::class.java).build()

        adapter = object : FirebaseRecyclerAdapter<Message, MessageViewHolder>(options)
        {
            override fun onBindViewHolder(@NonNull holder: MessageViewHolder, position: Int, model: Message)
            {
                holder.bind(model, currentUser!!, otherUser!!)
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder
            {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.chat_message_item, parent, false)
                return MessageViewHolder(v)
            }
        }



        adapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver()
        {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int)
            {
                super.onItemRangeInserted(positionStart, itemCount)


                scrollToBottom()
            }
        })

        //Populate Item into Adapter
        recyclerView.adapter = adapter

        scrollToBottom()

    }

    private fun scrollToBottom()
    {
        if (recyclerView != null && adapter != null && adapter?.itemCount != null)
        {
            recyclerView.scrollToPosition(adapter?.itemCount?.minus(1)!!)
        }
    }

    override fun onStart()
    {
        super.onStart()
        adapter?.startListening()
    }

    override fun onStop()
    {
        super.onStop()
        adapter?.stopListening()
    }

    private fun submitMessage()
    {
        val body = messageEditText.text.toString()

        if (TextUtils.isEmpty(body))
        {
            messageEditText.error = REQUIRED
            return
        }

        writeNewMessage(body)

    }

    private fun writeNewMessage(body: String)
    {

        val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().time)
        val message = Message(body, currentUser?.id!!, time, currentUser?.firstname!!)

        //mMessageReference?.ref?.push()?.setValue(message)

        channel.last_message = message.text

        //Utils.getDatabase().getReferenceFromUrl("https://bmybank-2146c.firebaseio.com/user-channels/${currentUser?.id}/channels").child("${channel.id_user_1}${channel.id_user_2}").setValue(channel)
        //Utils.getDatabase().getReferenceFromUrl("https://bmybank-2146c.firebaseio.com/user-channels/${otherUser?.id}/channels").child("${channel.id_user_1}${channel.id_user_2}").setValue(channel)

        val api = BmyBankApi.getInstance(this)

        val currentUserID = currentUser?.id
        val otherUserID = otherUser?.id

        log("sending chat essage with iduser1 = ${channel.id_user_1} and isuser2 = ${channel.id_user_2}")
        val moshi = Moshi.Builder().build()
        val jsonChannelAdapter = moshi.adapter(Channel::class.java)
        val jsonMessageAdapter = moshi.adapter(Message::class.java)

        val channelJson = jsonChannelAdapter.toJson(channel)
        val messageJson = jsonMessageAdapter.toJson(message)


        val sendChatMessageRequest = api.sendChatMessage(
                currentUserId = currentUserID!!,
                otherUserId = otherUserID!!,
                messageToJson = messageJson,
                channelToJson = channelJson
        )


        sendChatMessageRequest.enqueue(object : Callback<SImpleResponse>
        {
            override fun onResponse(call: Call<SImpleResponse>, response: Response<SImpleResponse>)
            {
                val chatResponse = response.body()
                if (chatResponse?.success != null && chatResponse?.success)
                {
                    log("successfuly sent chat message to firebase")
                }
            }


            override fun onFailure(call: Call<SImpleResponse>, t: Throwable)
            {
                log("unable to send chat message")
            }
        })

    }

    private fun loadUserThenDisplayTitle()
    {
        val api = BmyBankApi.getInstance(ctx)
        val otherUserid = if (currentUser?.id == channel.id_user_1) channel.id_user_2 else channel.id_user_1

        val findUserByIdRequest = api.findUserById(otherUserid)

        findUserByIdRequest.enqueue(object : Callback<UserResponse>
        {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>)
            {
                val userResponse = response.body()
                if (userResponse?.success != null && userResponse?.success)
                {
                    otherUser = userResponse.user
                    toolbarTitle = "${otherUser?.firstname} ${otherUser?.lastname}"
                    setUp()
                    adapter?.startListening()
                }
            }


            override fun onFailure(call: Call<UserResponse>, t: Throwable)
            {
                ecalle.com.bmybank.extensions.log("enable to find user with id $otherUserid")
            }
        })
    }
}