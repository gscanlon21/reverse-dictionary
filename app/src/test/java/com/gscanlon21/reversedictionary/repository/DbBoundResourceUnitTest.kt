package com.gscanlon21.reversedictionary.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gscanlon21.reversedictionary.BaseUnitTest
import com.gscanlon21.reversedictionary.BuildConfig
import com.gscanlon21.reversedictionary.repository.data.DbBoundResource
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
class DbBoundResourceUnitTest : BaseUnitTest() {
    @Test
    fun testDbSuccess_returnsFromDb() = testDispatcher.runBlockingTest {
        val nbr = object : DbBoundResource<String>() {
            override suspend fun loadFromDb(): Flow<String> = flowOf("1")
        }

        val flo = nbr.flow(Dispatchers.Main).toList(mutableListOf())

        assert(flo == listOf(
            ViewResource.WithData.Loading(null),
            ViewResource.WithData.Success("1")
        ))
    }
}
