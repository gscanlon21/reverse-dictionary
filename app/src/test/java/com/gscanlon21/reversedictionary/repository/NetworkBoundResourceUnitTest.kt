package com.gscanlon21.reversedictionary.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.volley.Cache
import com.android.volley.Response
import com.android.volley.VolleyError
import com.gscanlon21.reversedictionary.BaseUnitTest
import com.gscanlon21.reversedictionary.BuildConfig
import com.gscanlon21.reversedictionary.repository.data.NetworkBoundResource
import com.gscanlon21.reversedictionary.repository.data.ViewResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [BuildConfig.MIN_SDK_VERSION, BuildConfig.TARGET_SDK_TEST_VERSION])
class NetworkBoundResourceUnitTest : BaseUnitTest() {
    @Test
    fun testFetchSuccess_withData_returnsFromNetwork() = testDispatcher.runBlockingTest {
        val nbr = object : NetworkBoundResource<String, String>() {
            override suspend fun loadFromDb(): Flow<String> = flowOf("1")
            override suspend fun shouldFetch(data: Flow<String>) = true
            override suspend fun createCall(): Response<String> {
                return Response.success("2", Cache.Entry())
            }
            override suspend fun saveCallResult(item: String) = item
        }

        val flo = nbr.flow(Dispatchers.Main).toList(mutableListOf())

        assert(flo == listOf(
            ViewResource.WithData.Loading(null),
            ViewResource.WithData.Loading("1"),
            ViewResource.WithData.Success("2")
        ))
    }

    @Test
    fun testFetchFailure_withData_returnsError() = testDispatcher.runBlockingTest {
        val volleyError = VolleyError("")
        val nbr = object : NetworkBoundResource<String, String>() {
            override suspend fun loadFromDb(): Flow<String> = flowOf("1")
            override suspend fun shouldFetch(data: Flow<String>) = true
            override suspend fun createCall(): Response<String> {
                return Response.error(volleyError)
            }
            override suspend fun saveCallResult(item: String) = item
        }

        val flo = nbr.flow(Dispatchers.Main).toList(mutableListOf())

        assert(flo == listOf(
            ViewResource.WithData.Loading(null),
            ViewResource.WithData.Loading("1"),
            ViewResource.Error(volleyError)
        ))
    }

    @Test
    fun testNoFetch_withData_returnsFromDb() = testDispatcher.runBlockingTest {
        val nbr = object : NetworkBoundResource<String, String>() {
            override suspend fun loadFromDb(): Flow<String> = flowOf("1")
            override suspend fun shouldFetch(data: Flow<String>) = false
            override suspend fun createCall(): Response<String> {
                return Response.success("2", Cache.Entry())
            }
            override suspend fun saveCallResult(item: String) = item
        }

        val flo = nbr.flow(Dispatchers.Main).toList(mutableListOf())

        assert(flo == listOf(
            ViewResource.WithData.Loading(null),
            ViewResource.WithData.Success("1")
        ))
    }

    @Test
    fun testDefaultShouldFetch_withNoData_returnsTrue() = testDispatcher.runBlockingTest {
        val nbr = object : NetworkBoundResource<String, String>() {
            override suspend fun loadFromDb(): Flow<String> = flowOf()
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
    fun testDefaultShouldFetch_withData_returnsFalse() = testDispatcher.runBlockingTest {
        val nbr = object : NetworkBoundResource<String, String>() {
            override suspend fun loadFromDb(): Flow<String> = flowOf("1")
            override suspend fun createCall(): Response<String> {
                return Response.success("2", Cache.Entry())
            }
            override suspend fun saveCallResult(item: String) = item
        }

        val flo = nbr.flow(Dispatchers.Main).toList(mutableListOf())

        assert(flo == listOf(
                ViewResource.WithData.Loading(null),
                ViewResource.WithData.Success("1")
        ))
    }
}
