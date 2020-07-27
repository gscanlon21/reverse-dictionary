package com.gscanlon21.reversedictionary.repository.search.result

import com.gscanlon21.reversedictionary.repository.data.NetworkOnlyBoundResource
import com.gscanlon21.reversedictionary.service.WebService
import com.gscanlon21.reversedictionary.service.api.WordnikAudioModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class GetAudioUri(private val searchResultService: WebService.SearchResultService, private val word: String) :
        NetworkOnlyBoundResource<List<WordnikAudioModel>, List<WordnikAudioModel>>() {
    override suspend fun createCall() = searchResultService.getAudioUris(word)
    override suspend fun saveCallResult(item: List<WordnikAudioModel>): List<WordnikAudioModel> = item
}
