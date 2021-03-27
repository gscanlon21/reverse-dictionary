package com.gscanlon21.reversedictionary.vm

import androidx.test.core.app.ApplicationProvider
import com.gscanlon21.reversedictionary.BaseUnitTest
import com.gscanlon21.reversedictionary.core.repository.ApiType
import com.gscanlon21.reversedictionary.core.repository.ViewResource
import com.gscanlon21.reversedictionary.core.search.SearchResultItem
import com.gscanlon21.reversedictionary.db.search.SearchResultEntity
import com.gscanlon21.reversedictionary.db.search.WordOfTheDayEntity
import com.gscanlon21.reversedictionary.repository.search.SearchRepository
import com.gscanlon21.reversedictionary.test.TestCoroutine
import com.gscanlon21.reversedictionary.vm.search.SearchViewModel
import com.jraska.livedata.TestLifecycle
import com.jraska.livedata.TestObserver
import com.jraska.livedata.test
import io.mockk.coEvery
import io.mockk.mockkClass
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import java.time.Instant

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
        searchViewModel = SearchViewModel(ApplicationProvider.getApplicationContext(), searchRepository)
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

        val slot = slot<ApiType>()
        coEvery {
            searchRepository.lookup(any(), capture(slot))
        } returns flowOf(
            ViewResource.WithData.Success(
                listOf(
                    SearchResultEntity(
                        "aglet",
                        "Tip of a shoelace",
                        0,
                        ApiType.Datamuse.Definition
                    )
                )
            )
        )
    }

    @Test
    fun testWordOfTheDay() = testDispatcher.runBlockingTest {
        searchViewModel.getWordOfTheDay().test()
            .assertValue { it is ViewResource.WithData.Success<String> && it.data == "Syzygy" }
    }

    @Test
    fun testRandomWord() = testDispatcher.runBlockingTest {
        searchViewModel.getRandomWord().test()
            .assertValue { it is ViewResource.WithData.Success<String> && it.data == "Syzygy" }
    }

    @Test
    fun testSearchResultList_returnsSuccess() = testDispatcher.runBlockingTest {
        ApiType.Datamuse.values().forEach { resultType ->
            val testObserver = TestObserver.create<ViewResource<List<SearchResultItem>?>>()
            val testLifecycle = TestLifecycle.initialized()
            searchViewModel.resultList(resultType, "aglet").observe(testLifecycle, testObserver)

            testObserver
                .assertNoValue()

            testLifecycle.resume()

            testObserver
                .assertValue { it is ViewResource.WithData.Success<List<SearchResultItem>?> }

            testLifecycle.destroy()
        }
    }
}
