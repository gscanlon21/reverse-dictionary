package com.gscanlon21.reversedictionary.viewmodel

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gscanlon21.reversedictionary.BaseUnitTest
import com.gscanlon21.reversedictionary.BuildConfig
import com.gscanlon21.reversedictionary.db.search.result.SearchResultEntity
import com.gscanlon21.reversedictionary.repository.data.ApiType
import com.gscanlon21.reversedictionary.repository.data.ViewResource
import com.gscanlon21.reversedictionary.repository.search.result.SearchResultRepository
import com.gscanlon21.reversedictionary.ui.main.search.result.SearchResultItem
import com.gscanlon21.reversedictionary.vm.search.result.SearchResultViewModel
import com.jraska.livedata.TestLifecycle
import com.jraska.livedata.TestObserver
import io.mockk.coEvery
import io.mockk.mockkClass
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [BuildConfig.MIN_SDK_VERSION, BuildConfig.TARGET_SDK_TEST_VERSION])
class SearchResultViewModelUnitTest : BaseUnitTest() {
    private lateinit var searchResultRepository: SearchResultRepository
    private lateinit var searchResultViewModel: SearchResultViewModel

    override fun setupDependencies() {
        searchResultRepository = mockkClass(SearchResultRepository::class)
        searchResultViewModel = SearchResultViewModel(searchResultRepository)
    }

    override fun setupMocks() {
        val slot = slot<ApiType>()
        coEvery {
            searchResultRepository.lookup(any(), capture(slot))
        } returns flowOf(
            ViewResource.WithData.Success(
                listOf(
                    SearchResultEntity(
                        "aglet",
                        "Tip of a shoelace",
                        0,
                        ApiType.Anagramica.Anagram
                    )
                )
            )
        )
    }

    @Test
    fun testSearchResultList_returnsSuccess() = testDispatcher.runBlockingTest {
        ApiType.Datamuse.values().forEach { resultType ->
            val testObserver = TestObserver.create<ViewResource<List<SearchResultItem>>>()
            val testLifecycle = TestLifecycle.initialized()
            searchResultViewModel.resultList(resultType, "aglet").observe(testLifecycle, testObserver)

            testObserver
                .assertNoValue()

            testLifecycle.resume()

            testObserver
                .assertValue { it is ViewResource.WithData.Success<List<SearchResultItem>> }

            testLifecycle.destroy()
        }
    }
}
