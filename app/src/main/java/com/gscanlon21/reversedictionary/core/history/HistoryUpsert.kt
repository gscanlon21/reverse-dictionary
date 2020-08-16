package com.gscanlon21.reversedictionary.core.history

data class HistoryUpsert(
    val name: String,
    val pinned: Boolean? = null
)
