package com.gscanlon21.reversedictionary.vm.search.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gscanlon21.reversedictionary.repository.data.ViewResource
import com.gscanlon21.reversedictionary.repository.search.result.SearchResultRepository
import com.gscanlon21.reversedictionary.service.api.WordnikAudioModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

@ExperimentalCoroutinesApi
class MetaViewModel constructor(private val searchResultRepository: SearchResultRepository) : ViewModel() {
    private val scrabbleDict = mapOf(
        'a' to 1, 'b' to 3, 'c' to 3, 'd' to 2, 'e' to 1, 'f' to 4, 'g' to 2, 'h' to 4, 'i' to 1,
        'j' to 8, 'k' to 5, 'l' to 1, 'm' to 3, 'n' to 1, 'o' to 1, 'p' to 3, 'q' to 10, 'r' to 1,
        's' to 1, 't' to 1, 'u' to 1, 'v' to 4, 'w' to 4, 'x' to 8, 'y' to 4, 'z' to 10
    )

    fun scrabbleScore(word: String): Int {
        return word.fold(0) { sum, char -> sum + scrabbleDict.getOrDefault(char.toLowerCase(), 0) }
    }

    private val _audioUri = MutableLiveData<WordnikAudioModel?>()
    suspend fun getAudioUri(word: String): LiveData<WordnikAudioModel?> {
        if (_audioUri.value != null) { return _audioUri }
            searchResultRepository.getAudioUris(word).collect { viewResource ->
                _audioUri.value = when (viewResource) {
                is ViewResource.WithData -> viewResource.data?.firstOrNull()
                else -> null
            }
        }
        return _audioUri
    }
}
