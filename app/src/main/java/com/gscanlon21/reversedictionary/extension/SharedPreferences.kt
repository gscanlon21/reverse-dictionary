package com.gscanlon21.reversedictionary.extension

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.gscanlon21.reversedictionary.R
import com.gscanlon21.reversedictionary.ui.navigation.UiView

fun Context.defaultSharedPreferences(): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

fun SharedPreferences.emptyResultsHidden(context: Context) =
    this.getBoolean(context.getString(R.string.key_shared_preference_empty_results_hidden), true)

fun SharedPreferences.pagesToShow(context: Context): List<UiView.SearchResult> {
    return getStringSet(
        context.getString(R.string.key_shared_preference_pages_to_show),
        context.resources.getStringArray(R.array.search_result_preference).toSet()
    )!!
        .map { UiView.SearchResult.resValueOf(it, context) }
        .sortedBy { it.sortOrder }
}
