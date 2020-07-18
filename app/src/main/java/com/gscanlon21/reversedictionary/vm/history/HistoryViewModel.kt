package com.gscanlon21.reversedictionary.vm.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.gscanlon21.reversedictionary.db.history.HistoryEntity
import com.gscanlon21.reversedictionary.repository.data.ViewResource
import com.gscanlon21.reversedictionary.repository.data.map
import com.gscanlon21.reversedictionary.repository.history.HistoryRepository
import com.gscanlon21.reversedictionary.ui.main.history.HistoryItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map

@ExperimentalCoroutinesApi
class HistoryViewModel constructor(private val historyRepository: HistoryRepository) : ViewModel() {
    suspend fun historyList(): LiveData<ViewResource<List<HistoryItem>>> =
        historyRepository.getHistory().map { response ->
            when (response) {
                is ViewResource.WithData<List<HistoryEntity>> -> response.map { lst ->
                    lst?.sortedWith(compareByDescending<HistoryEntity> { it.pinned }.thenByDescending { it.lastModified })
                        ?.map { HistoryItem(it) }
                }
                is ViewResource.Error -> response
            }
        }.asLiveData()
}
