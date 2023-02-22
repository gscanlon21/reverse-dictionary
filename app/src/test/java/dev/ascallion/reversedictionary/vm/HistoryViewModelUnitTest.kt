package dev.ascallion.reversedictionary.vm

import com.jraska.livedata.test
import dev.ascallion.reversedictionary.BaseUnitTest
import dev.ascallion.reversedictionary.core.history.HistoryItem
import dev.ascallion.reversedictionary.core.repository.ViewResource
import dev.ascallion.reversedictionary.db.history.HistoryEntity
import dev.ascallion.reversedictionary.repository.history.HistoryRepository
import dev.ascallion.reversedictionary.test.TestCoroutine
import dev.ascallion.reversedictionary.vm.history.HistoryViewModel
import io.mockk.coEvery
import io.mockk.mockkClass
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import java.time.Instant

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
            ViewResource.WithData.Success(
                listOf(
                    HistoryEntity("First", false, Instant.EPOCH),
                    HistoryEntity("Second", false, Instant.MIN),
                    HistoryEntity("Third", false, Instant.MAX)
                )
            )
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
            .assertValue {
                it is ViewResource.WithData.Success<List<*>> &&
                    it.data == expectedOrder
            }
    }
}
