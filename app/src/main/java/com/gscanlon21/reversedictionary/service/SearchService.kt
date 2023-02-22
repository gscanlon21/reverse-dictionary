package dev.ascallion.reversedictionary.service

import com.android.volley.Cache
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import dev.ascallion.reversedictionary.core.repository.ApiType
import dev.ascallion.reversedictionary.core.service.DatamuseModel
import dev.ascallion.reversedictionary.extension.getInt
import dev.ascallion.reversedictionary.extension.getSequenceOrNull
import dev.ascallion.reversedictionary.extension.getString
import dev.ascallion.reversedictionary.extension.toSequence
import dev.ascallion.reversedictionary.service.api.Requests
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
