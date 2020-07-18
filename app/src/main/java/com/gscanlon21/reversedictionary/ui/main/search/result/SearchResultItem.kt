package com.gscanlon21.reversedictionary.ui.main.search.result

import android.text.Html
import com.gscanlon21.reversedictionary.db.search.result.SearchResultEntity
import com.gscanlon21.reversedictionary.repository.data.ApiType
import com.gscanlon21.reversedictionary.ui.main.adapter.IListItem
import com.gscanlon21.reversedictionary.ui.main.adapter.ListItemAdapter.Companion.VIEW_TYPE_DEFINITION
import com.gscanlon21.reversedictionary.ui.main.adapter.ListItemAdapter.Companion.VIEW_TYPE_NONE
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class SearchResultItem constructor(entity: SearchResultEntity) : IListItem {
    override val titleText = Html.fromHtml(entity.value, Html.FROM_HTML_MODE_LEGACY).toString()
    override val definition: String? = entity.extra?.maxBy { it.score }?.value
    override val pinned: Boolean? = null
    override val viewType = if (entity.type == ApiType.Datamuse.Definition) { VIEW_TYPE_DEFINITION } else { VIEW_TYPE_NONE }
}
