package com.gscanlon21.reversedictionary.viewmodel

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gscanlon21.reversedictionary.BaseUnitTest
import com.gscanlon21.reversedictionary.BuildConfig
import com.gscanlon21.reversedictionary.db.history.HistoryEntity
import com.gscanlon21.reversedictionary.repository.data.ViewResource
import com.gscanlon21.reversedictionary.repository.history.HistoryRepository
import com.gscanlon21.reversedictionary.ui.main.history.HistoryItem
import com.gscanlon21.reversedictionary.vm.history.HistoryViewModel
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
class HistoryViewModelUnitTest : BaseUnitTest() {
    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var historyRepository: HistoryRepository

    override fun setupDependencies() {
        historyRepository = mockkClass(HistoryRepository::class)
        historyViewModel = HistoryViewModel(historyRepository)
    }

    override fun setupMocks() {
        coEvery {
            historyRepository.getHistory()
        } returns flowOf(
            ViewResource.WithData.Success(listOf(
                HistoryEntity("First", false, Instant.EPOCH),
                HistoryEntity("Second", false, Instant.MIN),
                HistoryEntity("Third", false, Instant.MAX)
            ))
        )
    }

    @Test
    fun testHistoryLength_returnsAll() = testDispatcher.runBlockingTest {
        historyViewModel.historyList().test()
            .assertValue { it is ViewResource.WithData.Success<List<*>> && it.data!!.count() == 3 }
    }

    @Test
    fun testHistoryOrder_returnsDesc() = testDispatcher.runBlockingTest {
        val expectedOrder = listOf(
            HistoryItem(HistoryEntity("Third", false, Instant.MAX)),
            HistoryItem(HistoryEntity("First", false, Instant.EPOCH)),
            HistoryItem(HistoryEntity("Second", false, Instant.MIN))
        )
        historyViewModel.historyList().test()
            .assertValue { it is ViewResource.WithData.Success<List<*>> &&
                    it.data == expectedOrder }
    }
}
