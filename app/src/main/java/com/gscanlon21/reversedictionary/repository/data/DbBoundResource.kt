package com.gscanlon21.reversedictionary.repository.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/**
 * Emits an initial null [ViewResource.WithData.Loading],
 * ... then the result from the db is returned as [ViewResource.WithData.Success]
 * @param ResultType Type for the Resource data
 */
@ExperimentalCoroutinesApi
abstract class DbBoundResource<ResultType> : IDbBoundResource<ResultType> {
    suspend fun flow(dispatcher: CoroutineDispatcher = Dispatchers.IO): Flow<ViewResource<ResultType>> {
        return loadFromDb().map<ResultType, ViewResource<ResultType>> { ViewResource.WithData.Success(it) }
            .onStart { emit(ViewResource.WithData.Loading(null)) }
            .flowOn(dispatcher)
    }

    abstract override suspend fun loadFromDb(): Flow<ResultType>
}
