package dev.ascallion.reversedictionary.vm.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dev.ascallion.reversedictionary.core.history.HistoryItem
import dev.ascallion.reversedictionary.core.repository.ViewResource
import dev.ascallion.reversedictionary.core.repository.map
import dev.ascallion.reversedictionary.db.history.HistoryEntity
import dev.ascallion.reversedictionary.repository.history.HistoryRepository
import kotlinx.coroutines.flow.map

class HistoryViewModel constructor(private val historyRepository: HistoryRepository) : ViewModel() {
    val historyItems: ArrayList<HistoryItem> = arrayListOf()

    suspend fun historyList(): LiveData<ViewResource<List<HistoryItem>>> {
        return historyRepository.getHistory().map { response ->
            when (response) {
                is ViewResource.WithData<List<HistoryEntity>> -> response.map { list ->
                    list.sortedWith(compareByDescending<HistoryEntity> { it.pinned }.thenByDescending { it.lastModified })
                        .map { HistoryItem(it) }
                }
                is ViewResource.Error -> response
            }
        }.asLiveData()
    }
}
