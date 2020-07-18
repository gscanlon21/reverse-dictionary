package com.gscanlon21.reversedictionary

import android.content.ClipboardManager
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.gscanlon21.reversedictionary.db.SearchDb
import com.gscanlon21.reversedictionary.db.history.HistoryDao
import com.gscanlon21.reversedictionary.db.history.HistoryEntity
import com.gscanlon21.reversedictionary.ui.main.MainActivity
import io.mockk.every
import io.mockk.mockkObject
import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@LargeTest
class ListItemAdapterInstrumentedTest {
    private lateinit var scenario: ActivityScenario<MainActivity>
    private lateinit var db: SearchDb
    private val historyDao: HistoryDao get() = db.history()
    private var mIdlingResource: IdlingResource? = null

    @After
    @Throws(IOException::class)
    fun after() {
        db.close()
        if (mIdlingResource != null) { IdlingRegistry.getInstance().unregister(mIdlingResource) }
    }

    @Before
    fun before() {
        setupDependencies()
        setupMocks() // Mocks must be setup before activity is launched
        scenario = launchActivity()
        scenario.onActivity { activity ->
            mIdlingResource = activity.mainViewModel.getIdlingResource()
            IdlingRegistry.getInstance().register(mIdlingResource)
        }
    }

    private fun setupDependencies() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = SearchDb.create(context, useInMemory = true, allowMainThreadQueries = true)
    }

    private fun setupMocks() {
        mockkObject(SearchDb)

        every {
            SearchDb.getInstance(any())
        } returns db
    }

    private suspend fun setupInitialState() {
        db.clearAllTables()
        historyDao.insert(HistoryEntity("Old", false))
    }

    @Test
    fun testCopyToClipboard_returnsClippedItemText() {
        runBlocking {
            setupInitialState()
        }

        onView(withId(R.id.view_pager)).perform(swipeLeft()) // Navigate to history page

        // Long click on the recycler list item to invoke the function to copy its text to the clipboard
        onView(CoreMatchers.allOf(withId(R.id.list_item_text), isDisplayingAtLeast(90)))
            .perform(longClick())

        val context = ApplicationProvider.getApplicationContext<Context>()
        val clipboard = ContextCompat.getSystemService(context, ClipboardManager::class.java)
        val clippedText = clipboard!!.primaryClip!!.getItemAt(0).text

        assert(clippedText == "Old")
    }
}
