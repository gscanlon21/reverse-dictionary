package com.gscanlon21.reversedictionary.androidtest

import com.android.volley.Cache
import com.android.volley.Response
import com.gscanlon21.reversedictionary.core.repository.ApiType
import com.gscanlon21.reversedictionary.core.service.DatamuseModel
import com.gscanlon21.reversedictionary.service.SearchService

interface DefaultService : SearchService {
    override suspend fun datamuseLookup(
        term: String,
        type: ApiType.Datamuse
    ): Response<List<DatamuseModel>> {
        return Response.success(listOf(), Cache.Entry())
    }
}
