package com.gscanlon21.reversedictionary.repository.history

import com.gscanlon21.reversedictionary.db.history.HistoryDao
import com.gscanlon21.reversedictionary.db.history.HistoryEntity
import com.gscanlon21.reversedictionary.db.history.HistoryUpsertEntity
import com.gscanlon21.reversedictionary.repository.data.ViewResource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

@ExperimentalCoroutinesApi
class HistoryRepository constructor(
    private val historyDao: HistoryDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun upsert(entity: HistoryUpsertEntity) {
        withContext(Dispatchers.IO) {
            historyDao.upsert(entity)
        }
    }

    suspend fun getHistory(): Flow<ViewResource<List<HistoryEntity>>> {
        return GetHistory(historyDao).flow(dispatcher)
    }
}
