package dev.ascallion.reversedictionary.repository.history

import androidx.lifecycle.asFlow
import dev.ascallion.reversedictionary.core.repository.DbBoundResource
import dev.ascallion.reversedictionary.db.history.HistoryDao
import dev.ascallion.reversedictionary.db.history.HistoryEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class GetHistory(private val historyDao: HistoryDao) : DbBoundResource<List<HistoryEntity>> {
    override suspend fun loadFromDb() = historyDao.getAll().asFlow()
}
