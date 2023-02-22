package dev.ascallion.reversedictionary.db.search

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.ascallion.reversedictionary.core.repository.ApiType

@Dao
interface SearchDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMany(items: List<SearchResultEntity>)

    @Query("SELECT * FROM searchresult WHERE type = :type AND `key` = :key")
    suspend fun getList(type: ApiType, key: String): List<SearchResultEntity>

    @Query("SELECT * FROM searchresult WHERE type = :type AND `key` IN (:keys)")
    suspend fun getList(type: ApiType, keys: List<String>): List<SearchResultEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWordOfTheDay(item: WordOfTheDayEntity)

    /**
     * Retrieves the most recent saved [WordOfTheDayEntity]
     */
    @Query("SELECT * FROM wotd ORDER BY createdTime DESC LIMIT 1")
    suspend fun getWordOfTheDay(): WordOfTheDayEntity
}
