package com.gscanlon21.reversedictionary.repository.search

import android.content.Context
import com.gscanlon21.reversedictionary.core.repository.DbBoundResource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

@ExperimentalCoroutinesApi
class GetRandomWord(
    private val context: Context,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : DbBoundResource<String> {

    /**
     * Retrieves a random word from the en_us word list
     */
    override suspend fun loadFromDb() = flow {
        val words = withContext(dispatcher) {
            context.applicationContext.assets.open("words/en_US.dic")
                .reader(Charsets.UTF_8)
                .readLines()
        }

        emit(words.random())
    }
}
