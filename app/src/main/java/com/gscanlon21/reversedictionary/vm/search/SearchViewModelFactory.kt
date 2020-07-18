package com.gscanlon21.reversedictionary.vm.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gscanlon21.reversedictionary.repository.search.SearchRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * Factory for creating a [SearchViewModel] with a constructor that takes a
 * [SearchRepository]
 */
@ExperimentalCoroutinesApi

class SearchViewModelFactory(private val repository: SearchRepository) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SearchViewModel(repository) as T
    }
}
