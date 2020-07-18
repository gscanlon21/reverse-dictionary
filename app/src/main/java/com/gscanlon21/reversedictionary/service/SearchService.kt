package com.gscanlon21.reversedictionary.service

import com.android.volley.Cache
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.gscanlon21.reversedictionary.R
import com.gscanlon21.reversedictionary.service.api.Requests
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine

@ExperimentalCoroutinesApi
class SearchService private constructor(private val requests: Requests) {
    private val wotdUrl =
        "https://api.wordnik.com/v4/words.json/wordOfTheDay?api_key=" + requests.context.getString(
            R.string.wordnik_api_key
        )
    private val randomWordUrl = "https://api.wordnik.com/v4/words.json/randomWord?hasDictionaryDef=true&maxCorpusCount=-1&minDictionaryCount=1&maxDictionaryCount=-1&minLength=5&maxLength=-1&api_key=" + requests.context.getString(
        R.string.wordnik_api_key
    )

    suspend fun requestWordOfTheDay(): Response<String> {
        return suspendCancellableCoroutine { continuation ->
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.GET, wotdUrl, null,
                Response.Listener { response ->
                    continuation.resume(Response.success(response.getString(WORDNIK_WORD_KEY), Cache.Entry()))
                },
                Response.ErrorListener { error ->
                    continuation.resumeWithException(error)
                }
            )

            requests.queue.add(jsonObjectRequest)
        }
    }

    suspend fun requestRandomWord(): Response<String> {
        return suspendCancellableCoroutine { continuation ->
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.GET, randomWordUrl, null,
                Response.Listener { response ->
                    continuation.resume(Response.success(response.getString(WORDNIK_WORD_KEY), Cache.Entry()))
                },
                Response.ErrorListener { error ->
                    continuation.resumeWithException(error)
                }
            )

            requests.queue.add(jsonObjectRequest)
        }
    }

    companion object {
        const val WORDNIK_WORD_KEY = "word"

        @Volatile private var instance: SearchService? = null
        fun getInstance(requests: Requests) =
            instance ?: synchronized(this) {
                instance ?: SearchService(requests).also { instance = it }
            }
    }
}
