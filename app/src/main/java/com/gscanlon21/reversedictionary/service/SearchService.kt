package com.gscanlon21.reversedictionary.service

import com.android.volley.Cache
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.gscanlon21.reversedictionary.core.repository.ApiType
import com.gscanlon21.reversedictionary.core.service.DatamuseModel
import com.gscanlon21.reversedictionary.extension.getInt
import com.gscanlon21.reversedictionary.extension.getSequenceOrNull
import com.gscanlon21.reversedictionary.extension.getString
import com.gscanlon21.reversedictionary.extension.toSequence
import com.gscanlon21.reversedictionary.service.api.Requests
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

interface SearchService {
    suspend fun datamuseLookup(term: String, type: ApiType.Datamuse): Response<List<DatamuseModel>>
}

class SearchServiceImpl constructor(private val requests: Requests) : SearchService {
    override suspend fun datamuseLookup(term: String, type: ApiType.Datamuse): Response<List<DatamuseModel>> {
        return suspendCancellableCoroutine { continuation ->
            val datamuseUrl = "https://api.datamuse.com/words?md=d&${type.apiRoute}$term"
            val jsonObjectRequest = JsonArrayRequest(
                Request.Method.GET, datamuseUrl, null,
                { response ->
                    val words = response.toSequence<JSONObject>().map {
                        DatamuseModel(
                            it.getString(DatamuseModel.WORD_KEY, ""),
                            it.getInt(DatamuseModel.SCORE_KEY, 0),
                            it.getSequenceOrNull<String>(DatamuseModel.DEFS_KEY)?.toList()
                        )
                    }.toList()
                    continuation.resume(Response.success(words, Cache.Entry()))
                },
                { error ->
                    continuation.resumeWithException(error)
                }
            )

            requests.queue.add(jsonObjectRequest)
        }
    }
}
