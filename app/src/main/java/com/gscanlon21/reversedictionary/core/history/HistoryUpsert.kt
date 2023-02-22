package dev.ascallion.reversedictionary.core.history

data class HistoryUpsert(
    val name: String,
    val pinned: Boolean? = null
)
