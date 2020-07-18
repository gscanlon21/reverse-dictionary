package com.gscanlon21.reversedictionary.repository.search.result

import com.gscanlon21.reversedictionary.db.search.result.SearchResultDao
import com.gscanlon21.reversedictionary.db.search.result.SearchResultEntity
import com.gscanlon21.reversedictionary.repository.data.ApiType
import com.gscanlon21.reversedictionary.repository.data.NetworkBoundResource
import com.gscanlon21.reversedictionary.service.SearchResultService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf

@ExperimentalCoroutinesApi
class GetAnagram(private val searchResultDao: SearchResultDao, private val searchResultService: SearchResultService, private val word: String) :
        NetworkBoundResource<List<SearchResultEntity>, List<String>>() {
    override suspend fun loadFromDb() = flowOf(searchResultDao.getList(ApiType.Anagramica.Anagram, word))
    override suspend fun createCall() = searchResultService.getAnagrams(word)
    override suspend fun saveCallResult(item: List<String>): List<SearchResultEntity> {
        val entities = item.map { SearchResultEntity(word, it, 0, ApiType.Anagramica.Anagram) }
        searchResultDao.insertMany(entities)
        return entities
    }
}
