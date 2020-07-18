package com.gscanlon21.reversedictionary

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gscanlon21.reversedictionary.db.SearchDb
import com.gscanlon21.reversedictionary.db.history.HistoryDao
import com.gscanlon21.reversedictionary.db.search.SearchDao
import io.mockk.clearAllMocks
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [BuildConfig.MIN_SDK_VERSION, BuildConfig.TARGET_SDK_TEST_VERSION])
abstract class BaseUnitTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    protected val testDispatcher = TestCoroutineDispatcher()

    private var db: SearchDb? = null
    protected val historyDao: HistoryDao get() = db!!.history()
    protected val searchDao: SearchDao get() = db!!.search()

    open fun setupDependencies() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, SearchDb::class.java).allowMainThreadQueries().build()
    }

    open fun setupMocks() { }

    @Before
    open fun before() {
        Dispatchers.setMain(testDispatcher)

        setupDependencies()
        setupMocks()
    }

    @After
    @Throws(IOException::class)
    open fun after() {
        db?.close()

        Dispatchers.resetMain() // Reset main dispatcher to the original Main dispatcher
        testDispatcher.cleanupTestCoroutines()

        clearAllMocks()
    }
}
