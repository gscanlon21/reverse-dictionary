package com.gscanlon21.reversedictionary.repository.search

import android.content.Context
import com.android.volley.Cache
import com.android.volley.Response
import com.gscanlon21.reversedictionary.core.repository.NetworkBoundResource
import com.gscanlon21.reversedictionary.db.search.SearchDao
import com.gscanlon21.reversedictionary.db.search.WordOfTheDayEntity
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.singleOrNull

@ExperimentalCoroutinesApi
class GetWordOfTheDay(private val context: Context, private val searchDao: SearchDao) :
    NetworkBoundResource<WordOfTheDayEntity, String> {

    override suspend fun loadFromDb() = flowOf(searchDao.getWordOfTheDay())

    /**
     * @param data a stream of [WordOfTheDayEntity]
     * @return true if the last saved item is before the start of the day, otherwise false
     */
    override suspend fun shouldFetch(data: Flow<WordOfTheDayEntity>): Boolean {
        val localStartOfDay = LocalDate.now(ZoneId.systemDefault()).atStartOfDay(ZoneId.systemDefault()).toInstant()
        return data.singleOrNull()?.createdTime?.isBefore(localStartOfDay) ?: true
    }

    override suspend fun createCall(): Response<String> {
        val randomWord = GetRandomWord(context).loadFromDb().single()
        return Response.success(randomWord, Cache.Entry())
    }

    override suspend fun saveCallResult(item: String): WordOfTheDayEntity {
        val entity = WordOfTheDayEntity(item, Instant.now())
        searchDao.insertWordOfTheDay(entity)
        return entity
    }
}
