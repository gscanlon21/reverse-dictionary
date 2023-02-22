package dev.ascallion.reversedictionary.core.history

import dev.ascallion.reversedictionary.db.history.HistoryEntity

data class HistoryItem constructor(
    val title: String,
    val pinned: Boolean = false
) {
    constructor(historyEntity: HistoryEntity) : this(historyEntity.name, historyEntity.pinned)
}
