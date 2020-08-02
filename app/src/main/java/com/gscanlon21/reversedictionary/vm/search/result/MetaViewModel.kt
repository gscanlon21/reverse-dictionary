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
