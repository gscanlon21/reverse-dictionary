package dev.ascallion.reversedictionary.extension

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import dev.ascallion.reversedictionary.R
import dev.ascallion.reversedictionary.core.ui.UiView

fun Context.defaultSharedPreferences(): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

fun SharedPreferences.emptyResultsHidden(context: Context) =
    this.getBoolean(context.getString(R.string.key_shared_preference_empty_results_hidden), true)

fun SharedPreferences.translationLanguageCode(context: Context) =
    this.getString(context.getString(R.string.key_shared_preference_translation_language), context.getString(R.string.translation_language_code_spanish))

fun SharedPreferences.missingDefinitionsHidden(context: Context) =
    this.getBoolean(context.getString(R.string.key_shared_preference_no_definitions_hidden), false)

fun SharedPreferences.pagesToShow(context: Context): List<UiView.SearchResult> {
    return getStringSet(
        context.getString(R.string.key_shared_preference_pages_to_show),
        context.resources.getStringArray(R.array.search_result_preference).toSet()
    )!!
        .map { UiView.SearchResult.resValueOf(it, context) }
        .sortedBy { it.sortOrder }
}
