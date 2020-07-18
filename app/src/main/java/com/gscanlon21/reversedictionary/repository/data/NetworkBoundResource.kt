package com.gscanlon21.reversedictionary.repository.data

import com.android.volley.Response
import com.android.volley.VolleyError
import com.gscanlon21.reversedictionary.repository.data.INetworkOnlyBoundResource.Companion.networkSemaphore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.toList

/**
 * Will always emit an initial null [ViewResource.WithData.Loading]
 * If [shouldFetch]: the db response is returned as [ViewResource.WithData.Loading], the api response is returned as [ViewResource.WithData.Success]
 * else the db response is [ViewResource.WithData.Success]
 * @param ResultType Type for the Resource data
 * @param RequestType Type for the API response
 */
@ExperimentalCoroutinesApi
@Suppress("RemoveExplicitTypeArguments")
abstract class NetworkBoundResource<ResultType, RequestType> : INetworkBoundResource<ResultType, RequestType> {
    suspend fun flow(dispatcher: CoroutineDispatcher): Flow<ViewResource<ResultType>> {
        return kotlinx.coroutines.flow.flow<ViewResource<ResultType>> {
            val dbSource = loadFromDb()
            if (shouldFetch(dbSource)) {
                emitAll(dbSource.map { ViewResource.WithData.Loading(it) })

                val apiResult = try {
                    networkSemaphore.acquire()
                    createCall()
                } catch (ex: VolleyError) {
                    Response.error<RequestType>(ex)
                } finally {
                    networkSemaphore.release()
                }

                if (apiResult.isSuccess) {
                    emit(ViewResource.WithData.Success<ResultType>(saveCallResult(apiResult.result)))
                } else {
                    emit(ViewResource.Error(apiResult.error))
                }
            } else {
                emitAll(dbSource.map { ViewResource.WithData.Success(it) })
            }
        }
                .onStart { emit(ViewResource.WithData.Loading(null)) }
                .flowOn(dispatcher)
    }

    /**
     * Called to get the cached data from the database
     */
    abstract override suspend fun loadFromDb(): Flow<ResultType>

    /**
     * Called with the data in the database to decide whether to fetch
     * potentially updated data from the network.
     *
     * The default implementation returns true if there is no or 'empty' data
     */
    override suspend fun shouldFetch(data: Flow<ResultType>): Boolean {
        val lst = data.toList(mutableListOf())
        if (lst.count() == 0) { return true }
        return when (lst[0]) {
            is Iterable<*> -> lst.all { (it as Iterable<*>).count() == 0 }
            else -> lst.all { it == null }
        }
    }

    /**
     * Called to create the API call
     */
    abstract override suspend fun createCall(): Response<RequestType>

    /**
     * Called to save the result of the API response into the database
     */
    abstract override suspend fun saveCallResult(item: RequestType): ResultType
}
