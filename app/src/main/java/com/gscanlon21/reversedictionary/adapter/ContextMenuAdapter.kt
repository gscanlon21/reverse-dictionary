package dev.ascallion.reversedictionary.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import dev.ascallion.reversedictionary.R
import dev.ascallion.reversedictionary.extension.defaultSharedPreferences
import dev.ascallion.reversedictionary.extension.translationLanguageCode
import dev.ascallion.reversedictionary.ui.MainActivity

interface ContextMenuAdapter {
    val snackbarShowDuration: Int /* ms */ get() = 5000

    interface ContextMenuViewHolder {
        val contextMenuView: View
        val targetText: CharSequence
    }

    fun launchMainActivity(context: Context, text: CharSequence) {
        val mainIntent = Intent(context, MainActivity::class.java).apply {
            putExtra(MainActivity.EXTRA_SEARCH_TERM, text)
        }
        ContextCompat.startActivity(context, mainIntent, null)
    }

    fun translateText(context: Context, text: CharSequence): Boolean {
        val translateIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            component = ComponentName(GOOGLE_TRANSLATE_PACKAGE, GOOGLE_TRANSLATE_ACTIVITY)
        }

        if (context.packageManager.resolveActivity(translateIntent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
            context.startActivity(translateIntent)
        } else {
            val toLang = context.defaultSharedPreferences().translationLanguageCode(context)
            val link = "https://translate.google.com/#view=home&op=translate&sl=en&tl=$toLang&text=$text"
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
        }

        return true
    }

    fun copyToClipboard(view: View, text: CharSequence): Boolean {
        val clipboard = ContextCompat.getSystemService(view.context, ClipboardManager::class.java)
            ?: return false

        val clip = ClipData.newPlainText(text, text)
        clipboard.setPrimaryClip(clip)
        Snackbar.make(view, view.resources.getString(R.string.context_menu_copied, text), snackbarShowDuration).show()

        return true
    }

    fun showContextMenu(holder: ContextMenuViewHolder): Boolean {
        PopupMenu(holder.contextMenuView.context, holder.contextMenuView).apply {
            menuInflater.inflate(R.menu.listitem, this.menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.copy -> copyToClipboard(holder.contextMenuView, holder.targetText)
                    R.id.translate -> translateText(holder.contextMenuView.context, holder.targetText)
                    else -> false
                }
            }
            show()
        }

        return true
    }

    companion object {
        private const val GOOGLE_TRANSLATE_PACKAGE = "com.google.android.apps.translate"
        private const val GOOGLE_TRANSLATE_ACTIVITY = "com.google.android.apps.translate.TranslateActivity"
    }
}
