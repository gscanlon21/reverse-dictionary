package com.gscanlon21.reversedictionary.vm

import com.gscanlon21.reversedictionary.BaseUnitTest
import com.gscanlon21.reversedictionary.db.search.WordOfTheDayEntity
import com.gscanlon21.reversedictionary.repository.data.ViewResource
import com.gscanlon21.reversedictionary.repository.search.SearchRepository
import com.gscanlon21.reversedictionary.test.TestCoroutine
import com.gscanlon21.reversedictionary.vm.search.SearchViewModel
import com.jraska.livedata.test
import io.mockk.coEvery
import io.mockk.mockkClass
import java.time.Instant
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class SearchViewModelUnitTest : BaseUnitTest(), TestCoroutine {
    override val testDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var searchRepository: SearchRepository

    @Before
    fun before() {
        setupDependencies()
        setupMocks()
    }

    private fun setupDependencies() {
        searchRepository = mockkClass(SearchRepository::class)
        searchViewModel = SearchViewModel(searchRepository)
    }

    private fun setupMocks() {
        coEvery {
            searchRepository.getWordOfTheDay()
        }.returnsMany(
            flowOf(ViewResource.WithData.Success(WordOfTheDayEntity("Syzygy", Instant.now())))
        )

        coEvery {
            searchRepository.getRandomWord()
        }.returnsMany(
            flowOf(ViewResource.WithData.Success("Syzygy"))
        )
    }

    @Test
    fun testWordOfTheDay() = testDispatcher.runBlockingTest {
        searchViewModel.wordOfTheDay().test()
            .assertValue { it is ViewResource.WithData.Success<String> && it.data == "Syzygy" }
    }

    @Test
    fun testRandomWord() = testDispatcher.runBlockingTest {
        searchViewModel.randomWord().test()
            .assertValue { it is ViewResource.WithData.Success<String> && it.data == "Syzygy" }
    }
}
