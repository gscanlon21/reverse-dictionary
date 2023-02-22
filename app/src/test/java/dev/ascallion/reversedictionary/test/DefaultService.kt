package dev.ascallion.reversedictionary.test

import com.android.volley.Cache
import com.android.volley.Response
import dev.ascallion.reversedictionary.core.repository.ApiType
import dev.ascallion.reversedictionary.core.service.DatamuseModel
import dev.ascallion.reversedictionary.service.SearchService

interface DefaultService : SearchService {
    override suspend fun datamuseLookup(
        term: String,
        type: ApiType.Datamuse
    ): Response<List<DatamuseModel>> {
        return Response.success(listOf(), Cache.Entry())
    }
}
