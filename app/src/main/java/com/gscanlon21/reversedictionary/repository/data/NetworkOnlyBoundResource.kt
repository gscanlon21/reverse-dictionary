package com.gscanlon21.reversedictionary.repository.data

import com.android.volley.Response
import com.android.volley.VolleyError
import com.gscanlon21.reversedictionary.repository.data.INetworkOnlyBoundResource.Companion.networkSemaphore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart

/**
 * Emits an initial null [ViewResource.WithData.Loading],
 * ... then the result from the api is returned as [ViewResource.WithData.Success]
 * @param ResultType Type for the Resource data
 * @param RequestType Type for the API response
 */
@Suppress("RemoveExplicitTypeArguments")
@ExperimentalCoroutinesApi
abstract class NetworkOnlyBoundResource<ResultType, RequestType> : INetworkOnlyBoundResource<ResultType, RequestType> {
    suspend fun flow(dispatcher: CoroutineDispatcher): Flow<ViewResource<ResultType>> {
        return flow<ViewResource<ResultType>> {
            val apiResponse = try {
                networkSemaphore.acquire()
                createCall()
            } catch (ex: VolleyError) {
                Response.error<RequestType>(ex)
            } finally {
                networkSemaphore.release()
            }

            if (apiResponse.isSuccess) {
                emit(ViewResource.WithData.Success<ResultType>(saveCallResult(apiResponse.result)))
            } else {
                emit(ViewResource.Error(apiResponse.error))
            }
        }
            .onStart { emit(ViewResource.WithData.Loading(null)) }
            .flowOn(dispatcher)
    }

    // Called to create the API call.
    abstract override suspend fun createCall(): Response<RequestType>

    // Called to map the result of the API
    abstract override suspend fun saveCallResult(item: RequestType): ResultType
}
