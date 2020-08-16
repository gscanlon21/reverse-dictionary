package com.gscanlon21.reversedictionary.repository.search

import android.content.Context
import com.gscanlon21.reversedictionary.core.repository.ApiType
import com.gscanlon21.reversedictionary.core.repository.ViewResource
import com.gscanlon21.reversedictionary.db.search.SearchDao
import com.gscanlon21.reversedictionary.db.search.SearchResultEntity
import com.gscanlon21.reversedictionary.db.search.WordOfTheDayEntity
import com.gscanlon21.reversedictionary.service.SearchService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface SearchRepository {
    suspend fun lookup(term: String, type: ApiType): Flow<ViewResource<List<SearchResultEntity>>>
    suspend fun getAnagrams(word: String): Flow<ViewResource<List<String>>>
    suspend fun getWordOfTheDay(): Flow<ViewResource<WordOfTheDayEntity>>
    suspend fun getRandomWord(): Flow<ViewResource<String>>
}

@ExperimentalCoroutinesApi
class SearchRepositoryImpl private constructor(
    private val context: Context,
    private val searchService: SearchService,
    private val searchDao: SearchDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : SearchRepository {

    override suspend fun lookup(term: String, type: ApiType): Flow<ViewResource<List<SearchResultEntity>>> {
        return when (type) {
            is ApiType.Datamuse -> Lookup(searchDao, searchService, term, type)
            else -> throw NotImplementedError()
        }.flow(dispatcher)
    }

    override suspend fun getAnagrams(word: String): Flow<ViewResource<List<String>>> {
        if (!word.matches("^[a-z]*$".toRegex(RegexOption.IGNORE_CASE))) {
            return flowOf(ViewResource.Error(null))
        }

        return GetAnagrams(context, word).flow(dispatcher)
    }

    override suspend fun getWordOfTheDay(): Flow<ViewResource<WordOfTheDayEntity>> {
        return GetWordOfTheDay(context, searchDao).flow(dispatcher)
    }

    override suspend fun getRandomWord(): Flow<ViewResource<String>> {
        return GetRandomWord(context).flow(dispatcher)
    }

    companion object {
        @Volatile private var instance: SearchRepository? = null
        fun getInstance(
            context: Context,
            searchService: SearchService,
            searchDao: SearchDao,
            dispatcher: CoroutineDispatcher = Dispatchers.IO
        ): SearchRepository {
            return instance ?: synchronized(this) {
                instance ?: SearchRepositoryImpl(context, searchService, searchDao, dispatcher).also { instance = it }
            }
        }
    }
}
