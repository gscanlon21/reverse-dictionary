package dev.ascallion.reversedictionary.androidtest

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before

@ExperimentalCoroutinesApi
interface TestCoroutine {
    val testDispatcher: TestCoroutineDispatcher

    @Before
    fun coroutine_before() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun coroutine_after() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }
}
