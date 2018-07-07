package ecalle.com.bmybank.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import ecalle.com.bmybank.R
import ecalle.com.bmybank.realm.bo.Note
import ecalle.com.bmybank.realm.bo.Refund
import ecalle.com.bmybank.view_holders.NoteViewHolder
import ecalle.com.bmybank.view_holders.RefundViewHolder

/**
 * Created by Thomas Ecalle on 07/04/2018.
 */
class NotesAdapter(private var list: List<Note>) : RecyclerView.Adapter<NoteViewHolder>()
{
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int)
    {
        val note = list[position]
        holder.bind(note)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, itemType: Int): NoteViewHolder
    {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.note_item, viewGroup, false)
        return NoteViewHolder(view)
    }


    override fun getItemCount(): Int
    {
        return list.size
    }

}