package com.gscanlon21.reversedictionary.ui.main.history

import com.gscanlon21.reversedictionary.db.history.HistoryEntity
import com.gscanlon21.reversedictionary.ui.main.adapter.IListItem
import com.gscanlon21.reversedictionary.ui.main.adapter.ListItemAdapter
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
data class HistoryItem constructor(private val historyEntity: HistoryEntity) :
    IListItem {
    override val pinned = historyEntity.pinned
    override val titleText = historyEntity.name
    override var viewType = ListItemAdapter.VIEW_TYPE_FAVORITE
    override val definition: String? = null
}
