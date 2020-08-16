package com.gscanlon21.reversedictionary.repository.history

import com.gscanlon21.reversedictionary.core.history.HistoryUpsert
import com.gscanlon21.reversedictionary.core.repository.ViewResource
import com.gscanlon21.reversedictionary.db.history.HistoryDao
import com.gscanlon21.reversedictionary.db.history.HistoryEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

interface HistoryRepository {
    suspend fun upsert(entity: HistoryUpsert)
    suspend fun getHistory(): Flow<ViewResource<List<HistoryEntity>>>
}

@ExperimentalCoroutinesApi
class HistoryRepositoryImpl private constructor(
    private val historyDao: HistoryDao,
    private val dispatcher: CoroutineDispatcher
) : HistoryRepository {

    override suspend fun upsert(entity: HistoryUpsert) {
        withContext(Dispatchers.IO) {
            historyDao.upsert(entity)
        }
    }

    override suspend fun getHistory(): Flow<ViewResource<List<HistoryEntity>>> {
        return GetHistory(historyDao).flow(dispatcher)
    }

    companion object {
        @Volatile private var instance: HistoryRepository? = null
        fun getInstance(historyDao: HistoryDao, dispatcher: CoroutineDispatcher = Dispatchers.IO): HistoryRepository {
            return instance ?: synchronized(this) {
                instance ?: HistoryRepositoryImpl(historyDao, dispatcher).also { instance = it }
            }
        }
    }
}
