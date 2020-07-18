package com.gscanlon21.reversedictionary.vm.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gscanlon21.reversedictionary.repository.history.HistoryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * Factory for creating a [HistoryViewModel] with a constructor that takes a
 * [HistoryRepository]
 */
@ExperimentalCoroutinesApi
class HistoryViewModelFactory(
    private val repository: HistoryRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HistoryViewModel(repository) as T
    }
}
