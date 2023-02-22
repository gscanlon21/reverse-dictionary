package dev.ascallion.reversedictionary.repository

import dev.ascallion.reversedictionary.BaseUnitTest
import dev.ascallion.reversedictionary.core.repository.DbBoundResource
import dev.ascallion.reversedictionary.core.repository.ViewResource
import dev.ascallion.reversedictionary.test.TestCoroutine
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
        val nbr = object : DbBoundResource<String> {
            override suspend fun loadFromDb(): Flow<String> = flowOf("1")
        }

        val flo = nbr.flow(Dispatchers.Main).toList(mutableListOf())

        assert(
            flo == listOf(
                ViewResource.WithData.Loading(null),
                ViewResource.WithData.Success("1")
            )
        )
    }
}
