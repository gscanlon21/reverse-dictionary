package com.gscanlon21.reversedictionary.repository.search

import com.gscanlon21.reversedictionary.repository.data.NetworkOnlyBoundResource
import com.gscanlon21.reversedictionary.service.SearchService
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class GetRandomWord(private val searchService: SearchService) :
        NetworkOnlyBoundResource<String, String>() {
    override suspend fun createCall() = searchService.requestRandomWord()
    override suspend fun saveCallResult(item: String) = item
}
