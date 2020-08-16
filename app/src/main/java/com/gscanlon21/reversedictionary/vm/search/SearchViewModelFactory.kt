package com.gscanlon21.reversedictionary.vm.search

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gscanlon21.reversedictionary.repository.search.SearchRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * Factory for creating a [SearchViewModel] with a constructor that takes a
 * [Application] and [SearchRepository]
 */
@ExperimentalCoroutinesApi
class SearchViewModelFactory(private val application: Application, private val repository: SearchRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SearchViewModel(application, repository) as T
    }
}
