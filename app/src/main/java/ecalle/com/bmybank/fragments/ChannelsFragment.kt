package ecalle.com.bmybank.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import ecalle.com.bmybank.ChatDialogActivity
import ecalle.com.bmybank.LoanViewerActivity
import ecalle.com.bmybank.R
import ecalle.com.bmybank.adapters.ChannelsAdapter
import ecalle.com.bmybank.extensions.log
import ecalle.com.bmybank.firebase.ChannelsFinder
import ecalle.com.bmybank.firebase.ChannelsFinderCallbacks
import ecalle.com.bmybank.firebase.bo.Channel
import ecalle.com.bmybank.realm.RealmServices
import ecalle.com.bmybank.realm.bo.User
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.toast

/**
 * Created by Thomas Ecalle on 20/05/2018.
 */
class ChannelsFragment : Fragment(), ChannelsAdapter.OnChannelClickListener, ChannelsFinderCallbacks
{

    private lateinit var recyclerView: RecyclerView
    private lateinit var loader: ProgressBar
    private lateinit var errorView: LinearLayout
    private lateinit var errorText: TextView
    private var channels: MutableList<Channel> = mutableListOf()
    private lateinit var adapter: ChannelsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_channels, container, false)

        recyclerView = view.find(R.id.recyclerView)
        loader = view.find(R.id.loader)
        errorView = view.find(R.id.errorView)
        errorText = view.find(R.id.errorText)

        adapter = ChannelsAdapter(channels, this)
        recyclerView.layoutManager = LinearLayoutManager(ctx)

        recyclerView.adapter = adapter

        loadThenGetChannels()

        return view
    }

    protected fun loadThenGetChannels()
    {
        loader.visibility = View.VISIBLE
        errorView.visibility = View.GONE
        recyclerView.visibility = View.GONE

        getChannels()
    }

    private fun getChannels()
    {
        val user = RealmServices.getCurrentUser(ctx)
        val channelFinder = ChannelsFinder(ctx, this)

        channelFinder.start(user)
    }

    private fun showInfo(message: String = getString(R.string.no_results))
    {
        loader.visibility = View.GONE
        errorView.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        errorText.text = message
    }

    override fun onChannelFound(channel: Channel?)
    {
        log("onChannelFOund $channel")
        if (channel != null)
        {
            if (channels.contains(channel))
            {
                val index = channels.indexOf(channel)
                val initialStory = channels.get(index)
            }
            else
            {
                channels.add(channel)
            }

            adapter.notifyDataSetChanged()
            isEmptyListHandling(false)
        }
    }

    override fun onChannelClick(channel: Channel, user: User?)
    {
        val intent = Intent(ctx, ChatDialogActivity::class.java)
        intent.putExtra(ChatDialogActivity.DISCUSSION_KEY, channel)
        intent.putExtra(ChatDialogActivity.OTHER_USER_KEY, user)
        startActivity(intent)
    }

    override fun onChannelRemoved(channel: Channel?)
    {
        log("onChannelRemoved $channel")

        channels.remove(channel)
        adapter.notifyDataSetChanged()
        isEmptyListHandling(false)
    }

    override fun onNoChannelFound()
    {
        log("onNoChannelFOund")
        isEmptyListHandling(false)
    }

    override fun onStartSearching()
    {
        log("onStartSearching")
        isEmptyListHandling(true)
    }

    override fun onNetworkError()
    {
        log("onStartSearching")
        showInfo(getString(R.string.not_internet))
    }

    private fun isEmptyListHandling(isLoading: Boolean)
    {
        loader.visibility = if (isLoading) View.VISIBLE else View.GONE
        errorView.visibility = if (channels.isEmpty() && !isLoading) View.VISIBLE else View.GONE
        errorText.text = getString(R.string.no_channels)
        recyclerView.visibility = if (channels.isEmpty() && !isLoading) View.GONE else View.VISIBLE
    }
}