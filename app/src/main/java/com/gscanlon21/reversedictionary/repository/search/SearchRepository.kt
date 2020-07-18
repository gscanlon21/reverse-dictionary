package com.gscanlon21.reversedictionary.repository.search

import com.gscanlon21.reversedictionary.db.search.SearchDao
import com.gscanlon21.reversedictionary.db.search.WordOfTheDayEntity
import com.gscanlon21.reversedictionary.repository.data.ViewResource
import com.gscanlon21.reversedictionary.service.SearchService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

@ExperimentalCoroutinesApi
class SearchRepository private constructor(private val searchService: SearchService, private val searchDao: SearchDao, private val dispatcher: CoroutineDispatcher) {

    suspend fun getWordOfTheDay(): Flow<ViewResource<WordOfTheDayEntity>> {
        return GetWordOfTheDay(searchService, searchDao).flow(dispatcher)
    }

    suspend fun getRandomWord(): Flow<ViewResource<String>> {
        return GetRandomWord(searchService).flow(dispatcher)
    }

    companion object {
        @Volatile private var instance: SearchRepository? = null
        fun getInstance(searchService: SearchService, searchDao: SearchDao) =
            instance ?: synchronized(this) {
                instance ?: SearchRepository(searchService, searchDao, Dispatchers.IO).also { instance = it }
            }
    }
}
