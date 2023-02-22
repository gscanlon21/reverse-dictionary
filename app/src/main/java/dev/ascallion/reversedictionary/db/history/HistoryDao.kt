package dev.ascallion.reversedictionary.db.history

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import dev.ascallion.reversedictionary.core.history.HistoryUpsert

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: HistoryEntity): Long

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun update(item: HistoryEntity)

    @Transaction
    suspend fun upsert(entity: HistoryUpsert) {
        val existing = getOrNull(entity.name)
        if (existing != null) {
            update(HistoryEntity(entity, existing))
        } else {
            insert(HistoryEntity(entity, existing))
        }
    }

    @Query("SELECT * FROM history WHERE name = :name")
    suspend fun getOrNull(name: String): HistoryEntity?

    // Returning a LiveData<T> here and converting to Flow<T> in the Repo
    // When returning a type Flow<T> the UI isn't collecting a db update
    @Query("SELECT * FROM history")
    fun getAll(): LiveData<List<HistoryEntity>>
}
