package com.gscanlon21.reversedictionary.ui.main.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
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
import android.widget.ToggleButton
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.text.set
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.gscanlon21.reversedictionary.R
import com.gscanlon21.reversedictionary.ui.main.MainActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class ListItemAdapter<T : IListItem>(private val context: Context) : RecyclerView.Adapter<ListItemAdapter.MyViewHolder>() {
    data class MyViewHolder(val itemView: View, val text: TextView, val definition: TextView, val favButton: ToggleButton, val viewType: Int) : RecyclerView.ViewHolder(itemView)

    override fun getItemCount(): Int = results.count()
    override fun getItemId(position: Int) = results[position].hashCode().toLong()
    override fun getItemViewType(position: Int): Int = results[position].viewType

    private var favToggleCallback: ((word: String, newState: Boolean) -> Unit)? = null
    fun setOnFavToggled(callback: (word: String, newState: Boolean) -> Unit) { favToggleCallback = callback }

    var results: List<T> = emptyList()
        get() = field.distinctBy { it.titleText }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        val text = view.findViewById<TextView>(R.id.list_item_text)
        val definition = view.findViewById<TextView>(R.id.list_item_definition)
        val favToggle = view.findViewById<ToggleButton>(R.id.fav_toggle)

        if (viewType == VIEW_TYPE_FAVORITE) {
            favToggle.visibility = View.VISIBLE
            favToggle.setOnClickListener {
                favToggleCallback?.invoke(text.text.toString(), favToggle.isChecked)
            }
        }

        return MyViewHolder(view, text, definition, favToggle, viewType)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val listItem = results[position]

        holder.favButton.isChecked = listItem.pinned ?: false
        holder.text.setOnClickListener { launchMainActivity(results[holder.adapterPosition].titleText) }

        if (!listItem.definition.isNullOrBlank()) {
            holder.definition.visibility = View.VISIBLE
            holder.definition.text = highlightDefinitionQualifier(listItem.definition!!)
        } else {
            // This is redundant on initial create, but important for updates -- the holder may already have data that needs updating
            holder.definition.visibility = View.GONE
        }

        holder.text.text = if (listItem.viewType != VIEW_TYPE_DEFINITION) { listItem.titleText } else { highlightDefinitionQualifier(listItem.titleText) }

        holder.text.setOnLongClickListener {
            copyToClipboard(it, results[holder.adapterPosition].titleText)
            return@setOnLongClickListener true
        }
    }

    private fun launchMainActivity(text: String) {
        val i = Intent(context, MainActivity::class.java)
        i.putExtra(MainActivity.EXTRA_SEARCH_TERM, text)
        ContextCompat.startActivity(context, i, null)
    }

    private fun highlightDefinitionQualifier(definition: String): Spanned {
        val stringBuilder = SpannableStringBuilder(definition)
        val qualifierIdx = stringBuilder.toString().indexOf("\t")
        if (qualifierIdx > -1) {
            stringBuilder[0, qualifierIdx] = StyleSpan(Typeface.ITALIC)
            stringBuilder[0, qualifierIdx] = RelativeSizeSpan(.7f)
            val pxOfSp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15f, context.resources.displayMetrics)
            stringBuilder.setSpan(TabStopSpan.Standard(pxOfSp.toInt()), 0, stringBuilder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        return stringBuilder
    }

//    private fun highlightDefinitionQualifier(definition: String): Spanned {
//        val stringBuilder = SpannableStringBuilder(definition)
//        val qualifierIdx = stringBuilder.toString().indexOf("\t")
//        if (qualifierIdx > -1) {
//            val definitionParts = definition.split("\t")
//            return Html.fromHtml("<small><i>${definitionParts[0]}</i></small>  --   ${definitionParts[1]}", Html.FROM_HTML_MODE_LEGACY)
//        }
//        return SpannedString(definition)
//    }

    private fun copyToClipboard(view: View, text: String) {
        val clipboard = getSystemService(context, ClipboardManager::class.java)
        val clip = ClipData.newPlainText(text, text)
        clipboard?.setPrimaryClip(clip)
        Snackbar.make(view, "Copied to clipboard: $text", 5000)
            .show()
    }

    companion object {
        const val VIEW_TYPE_NONE = 0
        const val VIEW_TYPE_FAVORITE = 1
        const val VIEW_TYPE_DEFINITION = 2
    }
}
