package com.gscanlon21.reversedictionary.db

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gscanlon21.reversedictionary.BaseUnitTest
import com.gscanlon21.reversedictionary.BuildConfig
import com.gscanlon21.reversedictionary.db.search.WordOfTheDayEntity
import java.time.Instant
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [BuildConfig.MIN_SDK_VERSION, BuildConfig.TARGET_SDK_TEST_VERSION])
class SearchDbUnitTest : BaseUnitTest() {
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
