package com.gscanlon21.reversedictionary.core.history

import com.gscanlon21.reversedictionary.db.history.HistoryEntity

data class HistoryItem constructor(
    val title: String,
    val pinned: Boolean = false
) {
    constructor(historyEntity: HistoryEntity) : this(historyEntity.name, historyEntity.pinned)
}
