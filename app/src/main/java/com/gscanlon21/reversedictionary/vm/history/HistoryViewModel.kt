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

@ExperimentalCoroutinesApi
class HistoryViewModel constructor(private val historyRepository: HistoryRepository) : ViewModel() {
    suspend fun historyList(): LiveData<ViewResource<List<HistoryItem>>> =
        historyRepository.getHistory().map { response ->
            when (response) {
                is ViewResource.WithData<List<HistoryEntity>> -> response.map { lst ->
                    lst.sortedWith(compareByDescending<HistoryEntity> { it.pinned }.thenByDescending { it.lastModified })
                        .map { HistoryItem(it) }
                }
                is ViewResource.Error -> response
            }
        }.asLiveData()
}
