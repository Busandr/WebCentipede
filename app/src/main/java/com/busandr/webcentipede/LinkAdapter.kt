package com.busandr.webcentipede

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class LinkAdapter(
    private val context: Context, private val linkList: MutableList<Link>
) : RecyclerView.Adapter<LinkAdapter.LinkViewHolder>() {

    private val dbHelper = DatabaseHelper.DatabaseManager.getInstance(context)

    class LinkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var linkPic: ImageView = itemView.findViewById(R.id.link_pic)
        val linkName: TextView = itemView.findViewById(R.id.link_name)
        val removeButton: Button = itemView.findViewById(R.id.link_button)
    }

    val TAG = "LinkAdapter"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.link_item, parent, false)
        return LinkViewHolder(view)
    }

    override fun getItemCount(): Int {
        return linkList.size
    }

    override fun onBindViewHolder(holder: LinkViewHolder, position: Int) {
        val linkPosition = linkList.get(position)
        holder.linkName.text = linkPosition.name
        val bitmapFavicon =
            BitmapFactory.decodeByteArray(linkPosition.favicon, 0, linkPosition.favicon.size)
        val scaledBitmap =
            bitmapFavicon?.let { Bitmap.createScaledBitmap(bitmapFavicon, 64, 64, true) }
                ?: BitmapFactory.decodeFile(R.drawable.baseline_add_24.toString())
        holder.linkPic.setImageBitmap(scaledBitmap)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, HistoryActivity::class.java)
            intent.putExtra("url", linkPosition.url)
            context.startActivity(intent)
        }

        holder.removeButton.setOnClickListener {
            linkList.removeAt(position)
            val deleteCheck = dbHelper?.deleteLink(linkPosition.id)
            Log.i(TAG, "deleted link $deleteCheck")
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemCount)
        }
    }
}
