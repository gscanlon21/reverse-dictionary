package com.gscanlon21.reversedictionary.db.search

import androidx.room.Entity
import androidx.room.Ignore
import com.gscanlon21.reversedictionary.core.repository.ApiType

@Entity(tableName = "searchresult", primaryKeys = ["type", "key", "value"])
data class SearchResultEntity(
    val key: String, // Search Term
    val value: String,
    val score: Int,
    val type: ApiType,

    @Ignore
    val extra: List<SearchResultEntity>? // For Definitions
) {
    constructor(key: String, value: String, score: Int, type: ApiType) :
        this(key, value, score, type, null)
}
