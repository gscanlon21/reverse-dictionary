package com.gscanlon21.reversedictionary.vm

import com.gscanlon21.reversedictionary.BaseUnitTest
import com.gscanlon21.reversedictionary.core.history.HistoryItem
import com.gscanlon21.reversedictionary.core.repository.ViewResource
import com.gscanlon21.reversedictionary.db.history.HistoryEntity
import com.gscanlon21.reversedictionary.repository.history.HistoryRepository
import com.gscanlon21.reversedictionary.test.TestCoroutine
import com.gscanlon21.reversedictionary.vm.history.HistoryViewModel
import com.jraska.livedata.test
import io.mockk.coEvery
import io.mockk.mockkClass
import java.time.Instant
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test

class HistoryViewModelUnitTest : BaseUnitTest(), TestCoroutine {
    override val testDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()

    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var historyRepository: HistoryRepository

    @Before
    fun before() {
        setupDependencies()
        setupMocks()
    }

    private fun setupDependencies() {
        historyRepository = mockkClass(HistoryRepository::class)
        historyViewModel = HistoryViewModel(historyRepository)
    }

    private fun setupMocks() {
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
            .assertValue { it is ViewResource.WithData.Success<List<*>> && it.data.count() == 3 }
    }

    @Test
    fun testHistoryOrder_returnsDesc() = testDispatcher.runBlockingTest {
        val expectedOrder = listOf(
            HistoryItem(
                HistoryEntity("Third", false, Instant.MAX)
            ),
            HistoryItem(
                HistoryEntity("First", false, Instant.EPOCH)
            ),
            HistoryItem(
                HistoryEntity("Second", false, Instant.MIN)
            )
        )
        historyViewModel.historyList().test()
            .assertValue { it is ViewResource.WithData.Success<List<*>> &&
                        it.data == expectedOrder }
    }
}
