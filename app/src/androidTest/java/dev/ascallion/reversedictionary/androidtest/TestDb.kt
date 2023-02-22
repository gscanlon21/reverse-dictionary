package dev.ascallion.reversedictionary.androidtest

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.ascallion.reversedictionary.db.SearchDb
import dev.ascallion.reversedictionary.db.history.HistoryDao
import dev.ascallion.reversedictionary.db.search.SearchDao
import io.mockk.every
import io.mockk.mockkObject
import org.junit.After
import org.junit.Before
import java.io.IOException

interface TestDb {
    val db: SearchDb get() = SearchDb.getInstance(ApplicationProvider.getApplicationContext<Context>())
    val historyDao: HistoryDao get() = db.history()
    val searchDao: SearchDao get() = db.search()

    @Before
    fun db_before() {
        mockkObject(SearchDb)

        every {
            SearchDb.getInstance(any())
        } returns SearchDb.create(
            ApplicationProvider.getApplicationContext<Context>(),
            useInMemory = true,
            allowMainThreadQueries = true
        )
    }

    @After
    @Throws(IOException::class)
    fun db_after() {
        db.close()
    }
}
