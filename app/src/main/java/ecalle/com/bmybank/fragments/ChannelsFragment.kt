package ecalle.com.bmybank.fragments

import android.content.Intent
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.*
import ecalle.com.bmybank.ChatDialogActivity
import ecalle.com.bmybank.R
import ecalle.com.bmybank.adapters.ChannelsAdapter
import ecalle.com.bmybank.firebase.Utils
import ecalle.com.bmybank.firebase.bo.Channel
import ecalle.com.bmybank.realm.RealmServices
import ecalle.com.bmybank.realm.bo.User
import ecalle.com.bmybank.view_holders.ChannelViewHolder
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.ctx


/**
 * Created by Thomas Ecalle on 20/05/2018.
 */
class ChannelsFragment : Fragment(), ChannelsAdapter.OnChannelClickListener
{

    private var adapter: FirebaseRecyclerAdapter<Channel, ChannelViewHolder>? = null
    private var mDatabase: DatabaseReference? = null
    private var mChannelReference: DatabaseReference? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private lateinit var query: Query
    private lateinit var options: FirebaseRecyclerOptions<Channel>
    private var currentUser: User? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var loader: ProgressBar
    private lateinit var emptyView: LinearLayout


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_channels, container, false)

        recyclerView = view.find(R.id.recyclerView)
        loader = view.find(R.id.loader)
        emptyView = view.find(R.id.emptyView)


        currentUser = RealmServices.getCurrentUser(ctx)

        mDatabase = Utils.getDatabase().reference

        mChannelReference = Utils.getDatabase().getReferenceFromUrl("https://bmybank-2146c.firebaseio.com/user-channels/${currentUser?.id}/channels")


        recyclerView.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(ctx)
        recyclerView.layoutManager = layoutManager

        setUp()

        return view
    }

    private fun setUp()
    {

        query = mChannelReference?.orderByChild("list_message_id")!!

        options = FirebaseRecyclerOptions.Builder<Channel>().setQuery(query, Channel::class.java).build()

        adapter = object : FirebaseRecyclerAdapter<Channel, ChannelViewHolder>(options)
        {
            override fun onBindViewHolder(@NonNull holder: ChannelViewHolder, position: Int, model: Channel)
            {
                holder.bind(model, this@ChannelsFragment)
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder
            {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.channel_list_item, parent, false)
                return ChannelViewHolder(v)
            }
        }

        mChannelReference?.addListenerForSingleValueEvent(object : ValueEventListener
        {
            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
                //onDataChange called so remove progress bar
                loader.visibility = View.GONE

                val hasChildren = dataSnapshot.hasChildren()
                if (!hasChildren)
                {
                    emptyView.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(databaseError: DatabaseError)
            {

            }
        })





        adapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver()
        {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int)
            {
                super.onItemRangeInserted(positionStart, itemCount)
                loader.visibility = View.GONE
                emptyView.visibility = View.GONE

                recyclerView.scrollToPosition(adapter?.itemCount?.minus(1)!!)

            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int)
            {
                super.onItemRangeRemoved(positionStart, itemCount)
                if (itemCount == 1)
                {
                    emptyView.visibility = View.VISIBLE
                }
            }
        })

        //Populate Item into Adapter
        recyclerView.adapter = adapter

        recyclerView.scrollToPosition(adapter?.itemCount?.minus(1)!!)

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


    override fun onChannelClick(channel: Channel, user: User?)
    {
        val intent = Intent(ctx, ChatDialogActivity::class.java)
        intent.putExtra(ChatDialogActivity.DISCUSSION_KEY, channel)
        intent.putExtra(ChatDialogActivity.OTHER_USER_KEY, user)
        startActivity(intent)
    }


}