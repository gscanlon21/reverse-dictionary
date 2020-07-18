package com.gscanlon21.reversedictionary.repository.data

import com.android.volley.Response
import kotlinx.coroutines.sync.Semaphore

interface INetworkOnlyBoundResource<ResultType, RequestType> {
    // Called to create the API call.
    suspend fun createCall(): Response<RequestType>

    // Called to map the result of the API
    suspend fun saveCallResult(item: RequestType): ResultType

    companion object {
        val networkSemaphore = Semaphore(2)
    }
}
