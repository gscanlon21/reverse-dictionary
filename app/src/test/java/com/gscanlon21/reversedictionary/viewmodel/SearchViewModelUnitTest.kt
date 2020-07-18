package com.gscanlon21.reversedictionary.viewmodel

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gscanlon21.reversedictionary.BaseUnitTest
import com.gscanlon21.reversedictionary.BuildConfig
import com.gscanlon21.reversedictionary.db.search.WordOfTheDayEntity
import com.gscanlon21.reversedictionary.repository.data.ViewResource
import com.gscanlon21.reversedictionary.repository.search.SearchRepository
import com.gscanlon21.reversedictionary.vm.search.SearchViewModel
import com.jraska.livedata.test
import io.mockk.coEvery
import io.mockk.mockkClass
import java.time.Instant
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [BuildConfig.MIN_SDK_VERSION, BuildConfig.TARGET_SDK_TEST_VERSION])
class SearchViewModelUnitTest : BaseUnitTest() {
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var searchRepository: SearchRepository

    override fun setupDependencies() {
        searchRepository = mockkClass(SearchRepository::class)
        searchViewModel = SearchViewModel(searchRepository)
    }

    override fun setupMocks() {
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
