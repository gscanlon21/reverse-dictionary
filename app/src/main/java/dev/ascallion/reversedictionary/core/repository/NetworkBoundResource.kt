package dev.ascallion.reversedictionary.core.repository

import com.android.volley.Response
import com.android.volley.VolleyError
import dev.ascallion.reversedictionary.core.repository.NetworkOnlyBoundResource.Companion.networkSemaphore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.toList

/**
 * @param ResultType Type for the database data
 * @param RequestType Type for the API response
 */
@ExperimentalCoroutinesApi
interface NetworkBoundResource<ResultType, RequestType> :
    DbBoundResource<ResultType>,
    NetworkOnlyBoundResource<ResultType, RequestType> {

    /**
     * Emits an initial null [ViewResource.WithData.Loading]
     *
     * If [shouldFetch] returns true the db response is emitted as [ViewResource.WithData.Loading]
     * and the api response is emitted as [ViewResource.WithData.Success],
     * otherwise the db response is emitted as [ViewResource.WithData.Success]
     */
    override suspend fun flow(dispatcher: CoroutineDispatcher): Flow<ViewResource<ResultType>> {
        return kotlinx.coroutines.flow.flow {
            val dbSource = loadFromDb()
            if (shouldFetch(dbSource)) {
                emitAll(dbSource.map { ViewResource.WithData.Loading(it) })

                val apiResult = try {
                    networkSemaphore.acquire()
                    createCall()
                } catch (ex: VolleyError) {
                    Response.error(ex)
                } finally {
                    networkSemaphore.release()
                }

                if (apiResult.isSuccess) {
                    emit(ViewResource.WithData.Success(saveCallResult(apiResult.result!!)))
                } else {
                    emit(ViewResource.Error(apiResult.error))
                }
            } else {
                emitAll(
                    dbSource.map {
                        ViewResource.WithData.Success(it)
                    }
                )
            }
        }
            .onStart { emit(ViewResource.WithData.Loading(null)) }
            .flowOn(dispatcher)
    }

    /**
     * Called to get the cached data from the database
     */
    override suspend fun loadFromDb(): Flow<ResultType>

    /**
     * Called with the data in the database to decide whether to fetch
     * potentially updated data from the network.
     *
     * The default implementation returns true if there is no or 'empty' data
     */
    suspend fun shouldFetch(data: Flow<ResultType>): Boolean {
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
    override suspend fun createCall(): Response<RequestType>

    /**
     * Map and save the result from the API into the database
     */
    override suspend fun saveCallResult(item: RequestType): ResultType
}
