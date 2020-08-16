package com.gscanlon21.reversedictionary.vm.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.gscanlon21.reversedictionary.core.history.HistoryItem
import com.gscanlon21.reversedictionary.core.repository.ViewResource
import com.gscanlon21.reversedictionary.core.repository.map
import com.gscanlon21.reversedictionary.db.history.HistoryEntity
import com.gscanlon21.reversedictionary.repository.history.HistoryRepository
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
