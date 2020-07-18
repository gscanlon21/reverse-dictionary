package com.gscanlon21.reversedictionary.db.history

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "history",
    indices = [Index(value = ["name"], unique = true)])
data class HistoryEntity(
    @PrimaryKey val name: String,
    val pinned: Boolean,
    val lastModified: Instant = Instant.now()
) {
    constructor(entity: HistoryUpsertEntity, existing: HistoryEntity?) :
            this(entity.name, pinned = entity.pinned ?: existing?.pinned ?: false)
}

data class HistoryUpsertEntity(
    val name: String,
    val pinned: Boolean? = null
)
