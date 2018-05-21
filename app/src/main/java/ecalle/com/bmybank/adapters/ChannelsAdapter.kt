package ecalle.com.bmybank.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import ecalle.com.bmybank.R
import ecalle.com.bmybank.firebase.bo.Channel
import ecalle.com.bmybank.realm.bo.User
import ecalle.com.bmybank.view_holders.ChannelViewHolder

/**
 * Created by Thomas Ecalle on 07/04/2018.
 */
class ChannelsAdapter(private var list: List<Channel>, private var onChannelClickListener: OnChannelClickListener) : RecyclerView.Adapter<ChannelViewHolder>()
{

    interface OnChannelClickListener
    {
        fun onChannelClick(channel: Channel, user: User?)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, itemType: Int): ChannelViewHolder
    {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.channel_list_item, viewGroup, false)
        return ChannelViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int)
    {
        val channel = list[position]
        holder.bind(channel, onChannelClickListener)
    }

    override fun getItemCount(): Int
    {
        return list.size
    }

}