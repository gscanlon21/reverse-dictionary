package dev.ascallion.reversedictionary.adapter.search

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.text.style.TabStopSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.set
import androidx.recyclerview.widget.RecyclerView
import dev.ascallion.reversedictionary.R
import dev.ascallion.reversedictionary.adapter.ContextMenuAdapter
import dev.ascallion.reversedictionary.core.search.SearchResultItem

class SearchResultAdapter(private val results: List<SearchResultItem>) :
    RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder>(), ContextMenuAdapter {

    class SearchResultViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView), ContextMenuAdapter.ContextMenuViewHolder {

        val title: TextView = itemView.findViewById(R.id.list_item_title)
        val description: TextView? = itemView.findViewById(R.id.list_item_description)

        override val contextMenuView = itemView
        override val targetText: CharSequence get() = title.text.split("\t").last()
    }

    override fun getItemCount(): Int = results.count()
    override fun getItemId(position: Int) = results[position].hashCode().toLong()
    override fun getItemViewType(position: Int) = results[position].viewType

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return SearchResultViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        val listItem = results[position]

        holder.apply {
            title.text = highlightPartOfSpeech(holder.itemView, listItem.title)

            itemView.apply {
                setOnClickListener { launchMainActivity(holder.title.context, holder.targetText) }
                setOnLongClickListener { showContextMenu(holder) }
            }

            if (holder.description != null) {
                if (!listItem.description.isNullOrBlank()) {
                    holder.description.visibility = View.VISIBLE
                    holder.description.text = highlightPartOfSpeech(holder.itemView, listItem.description)
                } else {
                    // This is redundant on initial create, but important for updates -- the holder may already have data that needs updating
                    holder.description.visibility = View.GONE
                }
            }
        }
    }

    private fun highlightPartOfSpeech(holder: View, definition: String): Spanned {
        val stringBuilder = SpannableStringBuilder(definition)
        val qualifierIdx = stringBuilder.toString().indexOf("\t")
        if (qualifierIdx > -1) {
            stringBuilder[0, qualifierIdx] = StyleSpan(Typeface.ITALIC)
            stringBuilder[0, qualifierIdx] = RelativeSizeSpan(.7f)
            val pxOfSp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15f, holder.context.resources.displayMetrics)
            stringBuilder.setSpan(TabStopSpan.Standard(pxOfSp.toInt()), 0, stringBuilder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        return stringBuilder
    }
}
