package dev.ascallion.reversedictionary.core.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/**
 * @param ResultType Type for the database data
 */
@ExperimentalCoroutinesApi
interface DbBoundResource<ResultType> {
    suspend fun loadFromDb(): Flow<ResultType>

    /**
     * Emits an initial null [ViewResource.WithData.Loading]
     * The database result is emitted as [ViewResource.WithData.Success]
     */
    suspend fun flow(dispatcher: CoroutineDispatcher = Dispatchers.IO): Flow<ViewResource<ResultType>> {
        return loadFromDb().map<ResultType, ViewResource<ResultType>> {
            ViewResource.WithData.Success(it)
        }
            .onStart { emit(ViewResource.WithData.Loading(null)) }
            .flowOn(dispatcher)
    }
}
