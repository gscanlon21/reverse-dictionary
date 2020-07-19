package com.gscanlon21.reversedictionary.repository

import com.android.volley.Cache
import com.android.volley.Response
import com.android.volley.VolleyError
import com.gscanlon21.reversedictionary.BaseUnitTest
import com.gscanlon21.reversedictionary.repository.data.NetworkOnlyBoundResource
import com.gscanlon21.reversedictionary.repository.data.ViewResource
import com.gscanlon21.reversedictionary.test.TestCoroutine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

@ExperimentalCoroutinesApi
class NetworkOnlyBoundResourceUnitTest : BaseUnitTest(), TestCoroutine {
    override val testDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()

    @Test
    fun testFetchSuccess_returnsFromNetwork() = testDispatcher.runBlockingTest {
        val nbr = object : NetworkOnlyBoundResource<String, String>() {
            override suspend fun createCall(): Response<String> {
                return Response.success("2", Cache.Entry())
            }
            override suspend fun saveCallResult(item: String) = item
        }

        val flo = nbr.flow(Dispatchers.Main).toList(mutableListOf())

        assert(flo == listOf(
            ViewResource.WithData.Loading(null),
            ViewResource.WithData.Success("2")
        ))
    }

    @Test
    fun testFetchFailure_returnsError() = testDispatcher.runBlockingTest {
        val volleyError = VolleyError("")
        val nbr = object : NetworkOnlyBoundResource<String, String>() {
            override suspend fun createCall(): Response<String> {
                return Response.error(volleyError)
            }
            override suspend fun saveCallResult(item: String) = item
        }

        val flo = nbr.flow(Dispatchers.Main).toList(mutableListOf())

        assert(flo == listOf(
            ViewResource.WithData.Loading(null),
            ViewResource.Error(volleyError)
        ))
    }
}
