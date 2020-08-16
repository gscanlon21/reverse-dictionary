package com.gscanlon21.reversedictionary.repository.search

import android.content.Context
import com.gscanlon21.reversedictionary.core.repository.DbBoundResource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

@ExperimentalCoroutinesApi
class GetAnagrams(
    private val context: Context,
    private val word: String,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : DbBoundResource<List<String>> {

    /**
     * Retrieves the anagrams of [word]
     */
    override suspend fun loadFromDb() = flow {
        val words = withContext(dispatcher) {
            context.applicationContext.assets.open("words/en_US.dic")
                .reader(Charsets.UTF_8)
                .readLines()
        }

        val wordPermutations = listPermutations(StringBuffer(word))
        val anagrams = words.intersect(wordPermutations).toList()

        emit(anagrams)
    }

    /**
     * Lists all available permutations of characters in a given string
     *
     * @param stringBuffer the [StringBuffer] to find permutations for
     * @param index the index of the character to swap with all remaining characters in the string
     * @return the permutations of [stringBuffer]
     */
    private fun listPermutations(stringBuffer: StringBuffer, index: Int = 0): List<String> {
        val result = mutableListOf<String>()
        if (index == stringBuffer.length) { result.add(stringBuffer.toString()) } else {
            result.addAll(listPermutations(stringBuffer, index + 1))
            for (i in index + 1 until stringBuffer.length) { // Swap all other chars with first character
                swapChars(stringBuffer, index, i)
                result.addAll(listPermutations(stringBuffer, index + 1))
                swapChars(stringBuffer, i, index) // Restore previous string value
            }
        }
        return result
    }

    /**
     * Swaps two characters in a string
     *
     * The characters at positions [idx1] and [idx2] are swapped
     *
     * @param idx1 the index of the first character
     * @param idx2 the index of the second character
     */
    private fun swapChars(stringBuffer: StringBuffer, idx1: Int, idx2: Int) {
        val firstChar = stringBuffer[idx1]
        stringBuffer.setCharAt(idx1, stringBuffer[idx2])
        stringBuffer.setCharAt(idx2, firstChar)
    }
}
