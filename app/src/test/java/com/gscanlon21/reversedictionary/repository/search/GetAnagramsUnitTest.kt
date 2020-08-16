package com.gscanlon21.reversedictionary.repository.search

import androidx.test.core.app.ApplicationProvider
import com.gscanlon21.reversedictionary.BaseUnitTest
import com.gscanlon21.reversedictionary.test.TestCoroutine
import com.gscanlon21.reversedictionary.test.TestDb
import com.gscanlon21.reversedictionary.test.TestService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

@ExperimentalCoroutinesApi
class GetAnagramsUnitTest : BaseUnitTest(), TestCoroutine, TestDb, TestService {
    override val testDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()

    @Test
    fun test_returnsCorrectAnagrams() = testDispatcher.runBlockingTest {
        val anagramMap = mapOf(
            Pair("vile", arrayOf("evil", "levi", "live", "veil", "vile")),
            Pair("ripe", arrayOf("pier", "ripe")),
            Pair("apple", arrayOf("apple"))
        )

        anagramMap.forEach {
            val getAnagrams = GetAnagrams(ApplicationProvider.getApplicationContext(), it.key, testDispatcher)
            val anagramFlow = getAnagrams.loadFromDb()
            val anagrams = anagramFlow.first().sorted().toTypedArray()
            assert(anagrams.contentDeepEquals(it.value))
        }
    }
}
