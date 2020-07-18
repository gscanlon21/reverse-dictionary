package com.gscanlon21.reversedictionary.repository.data

import kotlinx.coroutines.flow.Flow

interface INetworkBoundResource<ResultType, RequestType> : IDbBoundResource<ResultType>, INetworkOnlyBoundResource<ResultType, RequestType> {
    suspend fun shouldFetch(data: Flow<ResultType>): Boolean
}
