package com.gscanlon21.reversedictionary.ui

import android.content.ClipboardManager
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.gscanlon21.reversedictionary.R
import com.gscanlon21.reversedictionary.androidtest.TestDb
import com.gscanlon21.reversedictionary.androidtest.TestService
import com.gscanlon21.reversedictionary.db.history.HistoryEntity
import com.gscanlon21.reversedictionary.ui.main.MainActivity
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
class ListItemAdapterInstrumentedTest : TestDb, TestService {
    private lateinit var scenario: ActivityScenario<MainActivity>
    private var mIdlingResource: IdlingResource? = null

    @After
    fun after() {
        if (mIdlingResource != null) { IdlingRegistry.getInstance().unregister(mIdlingResource) }
    }

    @Before
    fun before() {
        scenario = launchActivity()
        scenario.onActivity { activity ->
            mIdlingResource = activity.mainViewModel.getIdlingResource()
            IdlingRegistry.getInstance().register(mIdlingResource)
        }
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

        // Long click on the recycler list item to bring up the copy menu
        onView(CoreMatchers.allOf(withId(R.id.list_item_text), isDisplayingAtLeast(90)))
            .perform(longClick())

        // Click the copy button
        onView(withText(R.string.context_menu_copy)).perform(click())

        val context = ApplicationProvider.getApplicationContext<Context>()
        val clipboard = ContextCompat.getSystemService(context, ClipboardManager::class.java)
        val clippedText = clipboard!!.primaryClip!!.getItemAt(0).text

        assert(clippedText == "Old")
    }
}
