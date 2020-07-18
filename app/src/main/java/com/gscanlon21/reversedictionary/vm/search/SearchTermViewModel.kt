package com.gscanlon21.reversedictionary.vm.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gscanlon21.reversedictionary.db.history.HistoryUpsertEntity
import com.gscanlon21.reversedictionary.repository.history.HistoryRepository
import com.gscanlon21.reversedictionary.ui.main.search.SearchTerm
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

// Keep this final
@ExperimentalCoroutinesApi
class SearchTermViewModel private constructor(private val historyRepository: HistoryRepository) : ViewModel() {
    private val _searchTerm = MutableLiveData<SearchTerm?>()
    private val _searchWord = MutableLiveData<SearchTerm?>()
    private val _searchPhrase = MutableLiveData<SearchTerm?>()
    val searchPhrase: LiveData<SearchTerm?> get() = _searchPhrase
    val searchTerm: LiveData<SearchTerm?> get() = _searchTerm
    val searchWord: LiveData<SearchTerm?> get() = _searchWord

    internal fun setSearchTerm(searchTerm: SearchTerm?) {
        if (_searchTerm.value != searchTerm) {
            _searchTerm.value = searchTerm
            if (searchTerm?.term?.contains(SEARCH_WORD_PHRASE_DIFFERENTIATOR) != false) {
                _searchPhrase.value = searchTerm
            }
            if (searchTerm?.term?.contains(SEARCH_WORD_PHRASE_DIFFERENTIATOR) != true) {
                saveWord(searchTerm)
                _searchWord.value = searchTerm
            }
        }
    }

    private fun saveWord(word: SearchTerm?) {
        if (word?.term != null) {
            viewModelScope.launch {
                historyRepository.upsert(HistoryUpsertEntity(word.term))
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
