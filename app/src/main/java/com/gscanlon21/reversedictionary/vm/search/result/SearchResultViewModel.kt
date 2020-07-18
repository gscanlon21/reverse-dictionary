package com.gscanlon21.reversedictionary.vm.search.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.gscanlon21.reversedictionary.extension.nullIfEmpty
import com.gscanlon21.reversedictionary.repository.data.ApiType
import com.gscanlon21.reversedictionary.repository.data.ViewResource
import com.gscanlon21.reversedictionary.repository.data.map
import com.gscanlon21.reversedictionary.repository.search.result.SearchResultRepository
import com.gscanlon21.reversedictionary.ui.main.search.result.SearchResultItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest

@ExperimentalCoroutinesApi
class SearchResultViewModel constructor(private val searchResultRepository: SearchResultRepository) : ViewModel() {
    suspend fun resultList(type: ApiType, term: String): LiveData<ViewResource<List<SearchResultItem>>> {
        return searchResultRepository.lookup(term, type).mapLatest { item ->
            when (item) {
                is ViewResource.WithData -> item.map { data ->
                    data?.filter { i -> i.value.isNotBlank() && i.value.length > 1 }
                        .nullIfEmpty()
                        ?.sortedByDescending { it.score }
                        ?.map { SearchResultItem(it) }
                }
                is ViewResource.Error -> item
            }
        }.asLiveData()
    }
}
