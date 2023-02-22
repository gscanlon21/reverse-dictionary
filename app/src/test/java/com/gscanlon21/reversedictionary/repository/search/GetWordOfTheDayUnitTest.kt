package dev.ascallion.reversedictionary.repository.search

import androidx.test.core.app.ApplicationProvider
import dev.ascallion.reversedictionary.BaseUnitTest
import dev.ascallion.reversedictionary.db.search.WordOfTheDayEntity
import dev.ascallion.reversedictionary.test.TestCoroutine
import dev.ascallion.reversedictionary.test.TestDb
import dev.ascallion.reversedictionary.test.TestService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import java.time.Instant

@ExperimentalCoroutinesApi
class GetWordOfTheDayUnitTest : BaseUnitTest(), TestCoroutine, TestDb, TestService {
    override val testDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()

    private lateinit var getWordOfTheDay: GetWordOfTheDay

    @Before
    fun before() {
        setupDependencies()
    }

    private fun setupDependencies() {
        getWordOfTheDay = GetWordOfTheDay(ApplicationProvider.getApplicationContext(), searchDao)
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
