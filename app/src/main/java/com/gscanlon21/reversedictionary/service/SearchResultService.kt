package com.gscanlon21.reversedictionary.service

import com.android.volley.Cache
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.gscanlon21.reversedictionary.R
import com.gscanlon21.reversedictionary.extension.getInt
import com.gscanlon21.reversedictionary.extension.getSequenceOrNull
import com.gscanlon21.reversedictionary.extension.getString
import com.gscanlon21.reversedictionary.extension.toSequence
import com.gscanlon21.reversedictionary.repository.data.ApiType
import com.gscanlon21.reversedictionary.service.api.DatamuseModel
import com.gscanlon21.reversedictionary.service.api.Requests
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONObject

class SearchResultService constructor(private val requests: Requests) : WebService.SearchResultService {
    override suspend fun getAnagrams(word: String): Response<List<String>> {
        return suspendCancellableCoroutine { continuation ->
            val url = "http://anagramica.com/best/:$word"
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.GET, url, null,
                Response.Listener { response ->
                    continuation.resume(
                        Response.success(response.getJSONArray(ANAGRAMICA_WORD_KEY).toSequence<String>().toList(), Cache.Entry())
                    )
                },
                Response.ErrorListener { error ->
                    continuation.resumeWithException(error)
                }
            )

            requests.queue.add(jsonObjectRequest)
        }
    }

    override suspend fun getAudioUris(word: String): Response<List<String>?> {
        return suspendCancellableCoroutine { continuation ->
            val url =
                "https://api.wordnik.com/v4/word.json/$word/audio?api_key=${requests.context.getString(
                    R.string.wordnik_api_key
                )}"
            val jsonObjectRequest = JsonArrayRequest(
                Request.Method.GET, url, null,
                Response.Listener { response ->
                    val audioFiles = response.toSequence<JSONObject>().map {
                        if (it.has(WORDNIK_AUDIO_URL_KEY)) { it.getString(WORDNIK_AUDIO_URL_KEY) } else { "" }
                    }.toList()
                    continuation.resume(Response.success(audioFiles, Cache.Entry()))
                },
                Response.ErrorListener { error ->
                    continuation.resumeWithException(error)
                }
            )

            requests.queue.add(jsonObjectRequest)
        }
    }

    override suspend fun datamuseLookup(term: String, type: ApiType.Datamuse): Response<List<DatamuseModel>> {
        return suspendCancellableCoroutine { continuation ->
            val datamuseUrl = "https://api.datamuse.com/words?md=d&${type.apiRoute}$term"
            val jsonObjectRequest = JsonArrayRequest(
                Request.Method.GET, datamuseUrl, null,
                Response.Listener { response ->
                    val words = response.toSequence<JSONObject>().map {
                        DatamuseModel(
                            it.getString(DatamuseModel.WORD_KEY, ""),
                            it.getInt(DatamuseModel.SCORE_KEY, 0),
                            it.getSequenceOrNull<String>(DatamuseModel.DEFS_KEY)?.toList()
                        )
                    }.toList()
                    continuation.resume(Response.success(words, Cache.Entry()))
                },
                Response.ErrorListener { error ->
                    continuation.resumeWithException(error)
                }
            )

            requests.queue.add(jsonObjectRequest)
        }
    }

    companion object {
        const val WORDNIK_AUDIO_URL_KEY = "fileUrl"
        const val ANAGRAMICA_WORD_KEY = "best"
    }
}
