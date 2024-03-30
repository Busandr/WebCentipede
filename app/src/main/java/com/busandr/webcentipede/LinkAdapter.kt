package com.busandr.webcentipede

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LinkAdapter(private val context: Context, private val linkList: MutableList<Link>
) :
    RecyclerView.Adapter<LinkAdapter.LinkViewHolder>() {

    val dbHelper = DatabaseHelper(context)

    class LinkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        val linkName: TextView = itemView.findViewById(R.id.link_text)
        val removeButton: Button = itemView.findViewById(R.id.link_button)

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.link_item, parent, false)

        return LinkViewHolder(view)
    }

    override fun getItemCount(): Int {
        return linkList.size
    }
    override fun onBindViewHolder(holder: LinkViewHolder, position: Int) {
        val linkPosition = linkList[position]
        holder.linkName.text = linkPosition.name

        holder.itemView.setOnClickListener{

            val intent = Intent(context, HistoryActivity::class.java)
            context.startActivity(intent)
        }

        holder.removeButton.setOnClickListener{
            linkList.removeAt(position)
            notifyItemRemoved(position)
            dbHelper.deleteLink(linkPosition.id)
        }
    }
}
