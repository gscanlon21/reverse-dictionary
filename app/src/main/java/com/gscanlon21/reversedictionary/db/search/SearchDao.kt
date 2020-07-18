package com.gscanlon21.reversedictionary.db.search

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SearchDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWordOfTheDay(item: WordOfTheDayEntity)

    @Query("SELECT * FROM wotd ORDER BY createdTime DESC LIMIT 1")
    suspend fun getWordOfTheDay(): WordOfTheDayEntity
}
