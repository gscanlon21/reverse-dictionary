package com.gscanlon21.reversedictionary.adapter.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView
import com.gscanlon21.reversedictionary.R
import com.gscanlon21.reversedictionary.adapter.ContextMenuAdapter
import com.gscanlon21.reversedictionary.core.history.HistoryItem

class HistoryAdapter(private val results: List<HistoryItem>) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>(), ContextMenuAdapter {

    class HistoryViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView), ContextMenuAdapter.ContextMenuViewHolder {

        val text: TextView = itemView.findViewById(R.id.list_item_text)
        val favButton: ToggleButton = itemView.findViewById(R.id.fav_toggle)

        override val contextMenuView = itemView
        override val targetText: CharSequence get() = text.text
    }

    override fun getItemCount(): Int = results.count()
    override fun getItemId(position: Int) = results[position].hashCode().toLong()

    private var favToggleCallback: ((word: String, newState: Boolean) -> Unit)? = null

    fun setOnFavToggled(callback: (word: String, newState: Boolean) -> Unit) { favToggleCallback = callback }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_history, parent, false)
        val viewHolder = HistoryViewHolder(view)

        viewHolder.favButton.apply {
            visibility = View.VISIBLE
            setOnClickListener {
                favToggleCallback?.invoke(viewHolder.text.text.toString(), viewHolder.favButton.isChecked)
            }
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val listItem = results[position]

        holder.apply {
            favButton.isChecked = listItem.pinned
            itemView.apply {
                findViewById<TextView>(R.id.list_item_text).text = listItem.title
                setOnClickListener { launchMainActivity(holder.text.context, holder.text.text.toString()) }
                setOnLongClickListener { showContextMenu(holder) }
            }
        }
    }
}
