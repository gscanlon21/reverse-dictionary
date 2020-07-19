package com.gscanlon21.reversedictionary.androidtest

import com.android.volley.Cache
import com.android.volley.Response
import com.gscanlon21.reversedictionary.repository.data.ApiType
import com.gscanlon21.reversedictionary.service.Service.DatamuseModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
interface DefaultService : Service.SearchService, Service.SearchResultService {
    override suspend fun requestRandomWord(): Response<String> {
        return Response.success("", Cache.Entry())
    }

    override suspend fun requestWordOfTheDay(): Response<String> {
        return Response.success("", Cache.Entry())
    }

    override suspend fun datamuseLookup(
        term: String,
        type: ApiType.Datamuse
    ): Response<List<DatamuseModel>> {
        return Response.success(listOf(), Cache.Entry())
    }

    override suspend fun getAnagrams(word: String): Response<List<String>> {
        return Response.success(listOf(), Cache.Entry())
    }

    override suspend fun getAudioUris(word: String): Response<List<String>?> {
        return Response.success(listOf(), Cache.Entry())
    }
}
