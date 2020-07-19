package com.gscanlon21.reversedictionary.repository.search.result

import com.gscanlon21.reversedictionary.db.search.result.SearchResultDao
import com.gscanlon21.reversedictionary.db.search.result.SearchResultEntity
import com.gscanlon21.reversedictionary.repository.data.ApiType
import com.gscanlon21.reversedictionary.repository.data.ViewResource
import com.gscanlon21.reversedictionary.service.WebService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

@ExperimentalCoroutinesApi
class SearchResultRepository constructor(
    private val searchResultService: WebService.SearchResultService,
    private val searchResultDao: SearchResultDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun lookup(term: String, type: ApiType): Flow<ViewResource<List<SearchResultEntity>>> {
        return when (type) {
            ApiType.Anagramica.Anagram -> GetAnagram(searchResultDao, searchResultService, term)
            is ApiType.Datamuse -> Lookup(searchResultDao, searchResultService, term, type)
            else -> throw NotImplementedError()
        }.flow(dispatcher)
    }

    suspend fun getAudioUris(word: String): Flow<ViewResource<List<String>?>> {
        return GetAudioUri(searchResultService, word).flow(dispatcher)
    }
}
