package dev.ascallion.reversedictionary.db.search

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "wotd", indices = [Index(value = ["name"], unique = true)])
data class WordOfTheDayEntity(
    @PrimaryKey
    val name: String,
    // @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val createdTime: Instant // = Instant.now()
)
