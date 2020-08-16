package com.gscanlon21.reversedictionary.repository.search

import android.content.Context
import com.gscanlon21.reversedictionary.core.repository.DbBoundResource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

@ExperimentalCoroutinesApi
class GetRandomWord(private val searchService: WebService.SearchService) :
        NetworkOnlyBoundResource<String, String>() {
    override suspend fun createCall() = searchService.requestRandomWord()
    override suspend fun saveCallResult(item: String) = item
}
