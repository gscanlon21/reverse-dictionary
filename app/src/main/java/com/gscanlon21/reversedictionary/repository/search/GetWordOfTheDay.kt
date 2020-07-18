package com.gscanlon21.reversedictionary.repository.search

import com.gscanlon21.reversedictionary.db.search.SearchDao
import com.gscanlon21.reversedictionary.db.search.WordOfTheDayEntity
import com.gscanlon21.reversedictionary.repository.data.NetworkBoundResource
import com.gscanlon21.reversedictionary.service.SearchService
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.singleOrNull

@ExperimentalCoroutinesApi
class GetWordOfTheDay(private val searchService: SearchService, private val searchDao: SearchDao) :
        NetworkBoundResource<WordOfTheDayEntity, String>() {

    override suspend fun loadFromDb() = flowOf(searchDao.getWordOfTheDay())

    /**
     * @param data Flow<WordOfTheDayEntity>
     * @return Boolean if the last saved item is before the start of the day
     */
    override suspend fun shouldFetch(data: Flow<WordOfTheDayEntity>): Boolean {
        val localStartOfDay =
            LocalDate.now(ZoneId.systemDefault()).atStartOfDay(ZoneId.systemDefault()).toInstant()
        return data.singleOrNull()?.createdTime?.isBefore(localStartOfDay) ?: true
    }

    override suspend fun createCall() = searchService.requestWordOfTheDay()
    override suspend fun saveCallResult(item: String): WordOfTheDayEntity {
        val entity = WordOfTheDayEntity(item, Instant.now())
        searchDao.insertWordOfTheDay(entity)
        return entity
    }
}
