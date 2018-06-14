package ecalle.com.bmybank.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import ecalle.com.bmybank.R
import ecalle.com.bmybank.realm.bo.Refund
import ecalle.com.bmybank.view_holders.RefundViewHolder

/**
 * Created by Thomas Ecalle on 07/04/2018.
 */
class RefundsAdapter(private var list: List<Refund>) : RecyclerView.Adapter<RefundViewHolder>()
{
    override fun onBindViewHolder(holder: RefundViewHolder, position: Int)
    {
        val refund = list[position]
        holder.bind(refund)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, itemType: Int): RefundViewHolder
    {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.refund_item, viewGroup, false)
        return RefundViewHolder(view)
    }


    override fun getItemCount(): Int
    {
        return list.size
    }

}