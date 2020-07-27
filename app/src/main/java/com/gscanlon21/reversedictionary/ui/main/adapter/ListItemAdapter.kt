package com.gscanlon21.reversedictionary.ui.main.adapter

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
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
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.text.set
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.gscanlon21.reversedictionary.R
import com.gscanlon21.reversedictionary.extension.defaultSharedPreferences
import com.gscanlon21.reversedictionary.extension.translationLanguageCode
import com.gscanlon21.reversedictionary.ui.main.MainActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class ListItemAdapter<T : IListItem>(private val context: Context) : RecyclerView.Adapter<ListItemAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View, val viewType: Int) : RecyclerView.ViewHolder(itemView) {
        val text: TextView = itemView.findViewById(R.id.list_item_text)
        val definition: TextView = itemView.findViewById(R.id.list_item_definition)
        val favButton: ToggleButton = itemView.findViewById(R.id.fav_toggle)
    }

    override fun getItemCount(): Int = results.count()
    override fun getItemId(position: Int) = results[position].hashCode().toLong()
    override fun getItemViewType(position: Int): Int = results[position].viewType

    private var favToggleCallback: ((word: String, newState: Boolean) -> Unit)? = null
    fun setOnFavToggled(callback: (word: String, newState: Boolean) -> Unit) { favToggleCallback = callback }

    var results: List<T> = emptyList()
        get() = field.distinctBy { it.titleText }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        val myViewHolder = MyViewHolder(view, viewType)

        if (viewType == VIEW_TYPE_FAVORITE) {
            myViewHolder.favButton.visibility = View.VISIBLE
            myViewHolder.favButton.setOnClickListener {
                favToggleCallback?.invoke(myViewHolder.text.text.toString(), myViewHolder.favButton.isChecked)
            }
        }

        return myViewHolder
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val listItem = results[position]

        holder.favButton.isChecked = listItem.pinned ?: false
        holder.itemView.setOnClickListener { launchMainActivity(results[holder.adapterPosition].titleText) }

        if (!listItem.definition.isNullOrBlank()) {
            holder.definition.visibility = View.VISIBLE
            holder.definition.text = highlightDefinitionQualifier(listItem.definition!!)
        } else {
            // This is redundant on initial create, but important for updates -- the holder may already have data that needs updating
            holder.definition.visibility = View.GONE
        }

        holder.itemView.findViewById<TextView>(R.id.list_item_text).text = if (listItem.viewType != VIEW_TYPE_DEFINITION) { listItem.titleText } else { highlightDefinitionQualifier(listItem.titleText) }

        holder.itemView.setOnLongClickListener { showContextMenu(holder) }
    }

    private fun showContextMenu(holder: MyViewHolder): Boolean {
        val popup = PopupMenu(holder.itemView.context, holder.itemView)
        popup.menuInflater.inflate(R.menu.listitem, popup.menu)
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.copy -> copyToClipboard(holder.itemView, holder.text.text)
                R.id.translate -> translateText(holder.itemView, holder.text.text.split("\t").last())
                else -> false
            }
        }
        popup.show()
        return true
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

    private fun translateText(v: View, text: CharSequence): Boolean {
        try {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT, text)
            intent.component = ComponentName(
                "com.google.android.apps.translate",
                "com.google.android.apps.translate.TranslateActivity"
            )
            v.context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            val toLang = v.context.defaultSharedPreferences().translationLanguageCode(v.context)
            val link = "https://translate.google.com/#view=home&op=translate&sl=en&tl=$toLang&text=$text"
            v.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
        }
        return true
    }

    private fun copyToClipboard(v: View, text: CharSequence): Boolean {
        val clipboard = getSystemService(v.context, ClipboardManager::class.java)
        val clip = ClipData.newPlainText(text, text)
        clipboard?.setPrimaryClip(clip)
        Snackbar.make(v, "Copied to clipboard: $text", 5000)
            .show()
        return true
    }

    companion object {
        const val VIEW_TYPE_NONE = 0
        const val VIEW_TYPE_FAVORITE = 1
        const val VIEW_TYPE_DEFINITION = 2
    }
}
