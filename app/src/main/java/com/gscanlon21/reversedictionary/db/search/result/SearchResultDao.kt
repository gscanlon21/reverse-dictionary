package com.gscanlon21.reversedictionary.db.search.result

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gscanlon21.reversedictionary.repository.data.ApiType

@Dao
interface SearchResultDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMany(items: List<SearchResultEntity>)

    @Query("SELECT * FROM searchresult WHERE type = :type AND `key` = :key")
    suspend fun getList(type: ApiType, key: String): List<SearchResultEntity>

    @Query("SELECT * FROM searchresult WHERE type = :type AND `key` IN (:keys)")
    suspend fun getList(type: ApiType, keys: List<String>): List<SearchResultEntity>
}
