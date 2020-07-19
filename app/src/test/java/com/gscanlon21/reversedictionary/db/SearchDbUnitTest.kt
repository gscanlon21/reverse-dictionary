package com.gscanlon21.reversedictionary.db

import com.gscanlon21.reversedictionary.BaseUnitTest
import com.gscanlon21.reversedictionary.db.search.WordOfTheDayEntity
import com.gscanlon21.reversedictionary.test.TestCoroutine
import com.gscanlon21.reversedictionary.test.TestDb
import java.time.Instant
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

@ExperimentalCoroutinesApi
class SearchDbUnitTest : BaseUnitTest(), TestDb, TestCoroutine {
    override val testDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()

    @Test
    fun testWOTD_withOldAndNew_returnsNewer() = testDispatcher.runBlockingTest {
        // Setup initial state
        val now = Instant.now()
        val yesterday = now.minusMillis(86400000)
        searchDao.insertWordOfTheDay(WordOfTheDayEntity("old", yesterday))
        val expected = WordOfTheDayEntity("new", now)
        searchDao.insertWordOfTheDay(expected)

        // Test and assert
        val wotd = searchDao.getWordOfTheDay()
        assert(wotd == expected)
    }
}
