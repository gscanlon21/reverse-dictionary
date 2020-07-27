package com.gscanlon21.reversedictionary.service

import com.android.volley.Response
import com.gscanlon21.reversedictionary.repository.data.ApiType
import com.gscanlon21.reversedictionary.service.api.DatamuseModel
import com.gscanlon21.reversedictionary.service.api.WordnikAudioModel

interface WebService {
    interface SearchService {
        suspend fun requestWordOfTheDay(): Response<String>
        suspend fun requestRandomWord(): Response<String>
    }

    interface SearchResultService {
        suspend fun datamuseLookup(term: String, type: ApiType.Datamuse): Response<List<DatamuseModel>>
        suspend fun getAudioUris(word: String): Response<List<WordnikAudioModel>>
        suspend fun getAnagrams(word: String): Response<List<String>>
    }
}
