package dev.ascallion.reversedictionary.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.ascallion.reversedictionary.db.history.HistoryDao
import dev.ascallion.reversedictionary.db.history.HistoryEntity
import dev.ascallion.reversedictionary.db.search.SearchDao
import dev.ascallion.reversedictionary.db.search.SearchResultEntity
import dev.ascallion.reversedictionary.db.search.WordOfTheDayEntity

@Database(
    entities = [HistoryEntity::class, SearchResultEntity::class, WordOfTheDayEntity::class],
    version = 9,
    exportSchema = false
)
@TypeConverters(TypeConverter::class)
abstract class SearchDb : RoomDatabase() {
    companion object {
        fun create(context: Context, useInMemory: Boolean, allowMainThreadQueries: Boolean = false): SearchDb {
            val databaseBuilder = if (useInMemory) {
                Room.inMemoryDatabaseBuilder(context, SearchDb::class.java)
            } else {
                Room.databaseBuilder(context, SearchDb::class.java, "search.db")
            }
            if (allowMainThreadQueries) {
                databaseBuilder.allowMainThreadQueries()
            }
            return databaseBuilder
                .fallbackToDestructiveMigration()
                .build()
        }

        @Volatile private var instance: SearchDb? = null
        fun getInstance(context: Context): SearchDb {
            return instance ?: synchronized(this) {
                instance ?: create(context, false).also { instance = it }
            }
        }
    }

    abstract fun history(): HistoryDao
    abstract fun search(): SearchDao
}
