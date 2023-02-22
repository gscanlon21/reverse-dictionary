package dev.ascallion.reversedictionary.repository.search

import android.content.Context
import dev.ascallion.reversedictionary.core.repository.DbBoundResource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.util.Locale

@ExperimentalCoroutinesApi
class GetAnagrams(
    private val context: Context,
    private val word: String,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : DbBoundResource<List<String>> {

    /**
     * Provides a hash code unique to the characters contained in a string,
     * regardless of their order
     */
    @Suppress("EqualsOrHashCode")
    private class AnagramPrime(val string: String) {
        private val primeMap = mapOf(
            'a' to 2, 'b' to 3, 'c' to 5, 'd' to 7,
            'e' to 11, 'f' to 13, 'g' to 17, 'h' to 19, 'i' to 23, 'j' to 29, 'k' to 31,
            'l' to 37, 'm' to 41, 'n' to 43, 'o' to 47, 'p' to 53, 'q' to 59, 'r' to 61,
            's' to 67, 't' to 71, 'u' to 73, 'v' to 79, 'w' to 83, 'x' to 89, 'y' to 97,
            'z' to 101
        )

        override fun hashCode(): Int {
            return string.toLowerCase(Locale.ROOT).fold(1) { hash: Int, c: Char ->
                hash * (primeMap[c] ?: 1)
            }
        }
    }

    /**
     * Retrieves the anagrams of [word]
     *
     * Compares the potential anagrams using prime number hashing
     */
    override suspend fun loadFromDb() = flow {
        val wordHash = AnagramPrime(word).hashCode()
        val words = withContext(dispatcher) {
            context.applicationContext.assets.open("words/en_US.dic")
                .reader(Charsets.UTF_8)
                .readLines()
        }.map { AnagramPrime(it) }

        val anagrams = words
            .filter { it.hashCode() == wordHash }
            .map { it.string.toLowerCase(Locale.ROOT) }
            .filterNot { word.equals(it, ignoreCase = true) }
            .distinct()

        emit(anagrams)
    }
}
