package com.gscanlon21.reversedictionary.vm.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gscanlon21.reversedictionary.db.history.HistoryUpsertEntity
import com.gscanlon21.reversedictionary.repository.history.HistoryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

// Keep this final
@ExperimentalCoroutinesApi
class SearchTermViewModel private constructor(private val historyRepository: HistoryRepository) : ViewModel() {
    private val _searchTerm = MutableLiveData<String?>()
    private val _searchWord = MutableLiveData<String?>()
    private val _searchPhrase = MutableLiveData<String?>()
    val searchPhrase: LiveData<String?> get() = _searchPhrase
    val searchTerm: LiveData<String?> get() = _searchTerm
    val searchWord: LiveData<String?> get() = _searchWord

    internal fun setSearchTerm(searchTerm: String?) {
        if (_searchTerm.value != searchTerm) {
            _searchTerm.value = searchTerm
            if (searchTerm?.contains(SEARCH_WORD_PHRASE_DIFFERENTIATOR) != false) {
                _searchPhrase.value = searchTerm
            }
            if (searchTerm?.contains(SEARCH_WORD_PHRASE_DIFFERENTIATOR) != true) {
                saveWord(searchTerm)
                _searchWord.value = searchTerm
            }
        }
    }

    private fun saveWord(word: String?) {
        if (word != null) {
            viewModelScope.launch {
                historyRepository.upsert(HistoryUpsertEntity(word))
            }
        }
    }

    fun toggleFavWord(word: String, newState: Boolean) {
        viewModelScope.launch {
            historyRepository.upsert(HistoryUpsertEntity(word, pinned = newState))
        }
    }

    companion object {
        const val SEARCH_WORD_PHRASE_DIFFERENTIATOR = ' '

        // Cannot be a singleton, the super.onCleared method when the activity is recreated causing the viewModelScope to become inactive
        fun getInstance(historyRepository: HistoryRepository) = SearchTermViewModel(historyRepository)
    }
}
