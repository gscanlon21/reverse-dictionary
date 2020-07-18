package com.gscanlon21.reversedictionary.repository.search

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.volley.Cache
import com.android.volley.Response
import com.gscanlon21.reversedictionary.BaseUnitTest
import com.gscanlon21.reversedictionary.BuildConfig
import com.gscanlon21.reversedictionary.db.search.WordOfTheDayEntity
import com.gscanlon21.reversedictionary.service.SearchService
import io.mockk.coEvery
import io.mockk.mockkClass
import java.time.Instant
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [BuildConfig.MIN_SDK_VERSION, BuildConfig.TARGET_SDK_TEST_VERSION])
class GetWordOfTheDayUnitTest : BaseUnitTest() {
    private val searchService: SearchService = mockkClass(SearchService::class)
    private lateinit var getWordOfTheDay: GetWordOfTheDay

    override fun setupDependencies() {
        super.setupDependencies()
        getWordOfTheDay = GetWordOfTheDay(searchService, searchDao)
    }

    override fun setupMocks() {
        coEvery {
            searchService.requestWordOfTheDay()
        }.returnsMany(Response.success("Syzygy", Cache.Entry()), Response.success("Brilliancy", Cache.Entry()))
    }

    @Test
    fun testShouldFetch_withOldEntry_returnsTrue() = testDispatcher.runBlockingTest {
        searchDao.insertWordOfTheDay(WordOfTheDayEntity("Old", Instant.EPOCH))

        val loadFromDb = getWordOfTheDay.loadFromDb()
        assert(loadFromDb.toList(mutableListOf()) == listOf(WordOfTheDayEntity("Old", Instant.EPOCH)))

        val shouldFetch = getWordOfTheDay.shouldFetch(loadFromDb)
        assert(shouldFetch)
    }

    @Test
    fun testShouldFetch_withNoEntry_returnsTrue() = testDispatcher.runBlockingTest {
        val loadFromDb = getWordOfTheDay.loadFromDb()
        assert(loadFromDb.toList(mutableListOf()) == listOf(null))

        val shouldFetch = getWordOfTheDay.shouldFetch(loadFromDb)
        assert(shouldFetch)
    }

    @Test
    fun testShouldFetch_withNewEntry_returnsFalse() = testDispatcher.runBlockingTest {
        val now = Instant.now()
        searchDao.insertWordOfTheDay(WordOfTheDayEntity("New", now))

        val loadFromDb = getWordOfTheDay.loadFromDb()
        assert(loadFromDb.toList(mutableListOf()) == listOf(WordOfTheDayEntity("New", now)))

        val shouldFetch = getWordOfTheDay.shouldFetch(loadFromDb)
        assert(!shouldFetch)
    }
}
