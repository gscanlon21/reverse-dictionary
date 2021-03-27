package com.gscanlon21.reversedictionary.core.repository

import com.android.volley.Response
import com.android.volley.VolleyError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.sync.Semaphore

/**
 * @param ResultType Type for the Resource data
 * @param RequestType Type for the API response
 */
@ExperimentalCoroutinesApi
interface NetworkOnlyBoundResource<ResultType, RequestType> {

    /**
     * Emits an initial null [ViewResource.WithData.Loading]
     * The result from the api is returned as [ViewResource.WithData.Success]
     */
    suspend fun flow(dispatcher: CoroutineDispatcher): Flow<ViewResource<ResultType>> {
        return flow {
            val apiResponse = try {
                networkSemaphore.acquire()
                createCall()
            } catch (ex: VolleyError) {
                Response.error<RequestType>(ex)
            } finally {
                networkSemaphore.release()
            }

            if (apiResponse.isSuccess) {
                emit(ViewResource.WithData.Success(saveCallResult(apiResponse.result!!)))
            } else {
                emit(ViewResource.Error(apiResponse.error))
            }
        }
            .onStart { emit(ViewResource.WithData.Loading(null)) }
            .flowOn(dispatcher)
    }

    /**
     * Called to create the API call
     */
    suspend fun createCall(): Response<RequestType>

    /**
     * Map the result from the API
     */
    suspend fun saveCallResult(item: RequestType): ResultType

    companion object {
        // Used to limit the number of concurrent network requests
        val networkSemaphore = Semaphore(2)
    }
}
