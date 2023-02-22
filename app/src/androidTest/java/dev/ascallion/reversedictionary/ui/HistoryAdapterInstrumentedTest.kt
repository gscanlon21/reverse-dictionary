package dev.ascallion.reversedictionary.ui

/**
 * Animations and transitions must be disabled on the target device.
 *
 * FIXME:
 * This is disabled because left swipe on the view pager no longer works.
 */
/*
@RunWith(AndroidJUnit4::class)
@LargeTest
@OptIn(ExperimentalCoroutinesApi::class)
class HistoryAdapterInstrumentedTest : TestDb, TestService {
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
*/
