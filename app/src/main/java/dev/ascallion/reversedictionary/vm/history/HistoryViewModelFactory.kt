package dev.ascallion.reversedictionary.vm.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.ascallion.reversedictionary.repository.history.HistoryRepository

/**
 * Factory for creating a [HistoryViewModel] with a constructor that takes a [HistoryRepository]
 */
class HistoryViewModelFactory(private val repository: HistoryRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HistoryViewModel(repository) as T
    }
}
