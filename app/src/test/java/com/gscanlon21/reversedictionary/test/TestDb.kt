package com.gscanlon21.reversedictionary.test

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.gscanlon21.reversedictionary.db.SearchDb
import com.gscanlon21.reversedictionary.db.history.HistoryDao
import com.gscanlon21.reversedictionary.db.search.SearchDao
import io.mockk.every
import io.mockk.mockkObject
import java.io.IOException
import org.junit.After
import org.junit.Before

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
