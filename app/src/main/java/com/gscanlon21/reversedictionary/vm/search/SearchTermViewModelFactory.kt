package com.gscanlon21.reversedictionary.vm.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gscanlon21.reversedictionary.repository.history.HistoryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * Factory for creating a [SearchTermViewModel] with a constructor that takes a [HistoryRepository]
 */
@ExperimentalCoroutinesApi
class SearchTermViewModelFactory(private val repository: HistoryRepository) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SearchTermViewModel.getInstance(repository) as T
    }
}
