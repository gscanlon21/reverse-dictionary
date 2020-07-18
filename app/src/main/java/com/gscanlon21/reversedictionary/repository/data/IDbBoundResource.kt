package com.gscanlon21.reversedictionary.repository.data

import kotlinx.coroutines.flow.Flow

interface IDbBoundResource<ResultType> {
    suspend fun loadFromDb(): Flow<ResultType>
}
