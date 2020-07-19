package com.gscanlon21.reversedictionary.repository

import com.gscanlon21.reversedictionary.BaseUnitTest
import com.gscanlon21.reversedictionary.repository.data.DbBoundResource
import com.gscanlon21.reversedictionary.repository.data.ViewResource
import com.gscanlon21.reversedictionary.test.TestCoroutine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

@ExperimentalCoroutinesApi
class DbBoundResourceUnitTest : BaseUnitTest(), TestCoroutine {
    override val testDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()

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
