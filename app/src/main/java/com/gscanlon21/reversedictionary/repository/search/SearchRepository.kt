package com.gscanlon21.reversedictionary.repository.search

import com.gscanlon21.reversedictionary.db.search.SearchDao
import com.gscanlon21.reversedictionary.db.search.WordOfTheDayEntity
import com.gscanlon21.reversedictionary.repository.data.ViewResource
import com.gscanlon21.reversedictionary.service.WebService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

@ExperimentalCoroutinesApi
class SearchRepository constructor(
    private val searchService: WebService.SearchService,
    private val searchDao: SearchDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun getWordOfTheDay(): Flow<ViewResource<WordOfTheDayEntity>> {
        return GetWordOfTheDay(searchService, searchDao).flow(dispatcher)
    }

    suspend fun getRandomWord(): Flow<ViewResource<String>> {
        return GetRandomWord(searchService).flow(dispatcher)
    }
}
