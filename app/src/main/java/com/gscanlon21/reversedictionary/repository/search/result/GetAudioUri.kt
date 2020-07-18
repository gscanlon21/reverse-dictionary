package com.gscanlon21.reversedictionary.repository.search.result

import com.gscanlon21.reversedictionary.repository.data.NetworkOnlyBoundResource
import com.gscanlon21.reversedictionary.service.SearchResultService
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class GetAudioUri(private val searchResultService: SearchResultService, private val word: String) :
        NetworkOnlyBoundResource<List<String>?, List<String>?>() {
    override suspend fun createCall() = searchResultService.getAudioUris(word)
    override suspend fun saveCallResult(item: List<String>?): List<String>? = item
}
