package dev.ascallion.reversedictionary.repository.search

import androidx.test.core.app.ApplicationProvider
import dev.ascallion.reversedictionary.BaseUnitTest
import dev.ascallion.reversedictionary.test.TestCoroutine
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class GetRandomWordUnitTest : BaseUnitTest(), TestCoroutine {
    override val testDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()

    private lateinit var getRandomWord: GetRandomWord

    @Before
    fun before() {
        setupDependencies()
    }

    private fun setupDependencies() {
        getRandomWord = GetRandomWord(ApplicationProvider.getApplicationContext(), testDispatcher)
    }

    @Test
    fun test_returnsSuccess() = testDispatcher.runBlockingTest {
        val loadFromDb = getRandomWord.loadFromDb()
        assert(loadFromDb.toList(mutableListOf()).count() == 1)
    }
}
