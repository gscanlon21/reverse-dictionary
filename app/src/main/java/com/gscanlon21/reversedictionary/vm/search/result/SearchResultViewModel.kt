package com.gscanlon21.reversedictionary.vm.search.result

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.gscanlon21.reversedictionary.ReverseDictionaryApp
import com.gscanlon21.reversedictionary.extension.defaultSharedPreferences
import com.gscanlon21.reversedictionary.extension.missingDefinitionsHidden
import com.gscanlon21.reversedictionary.extension.nullIfEmpty
import com.gscanlon21.reversedictionary.repository.data.ApiType
import com.gscanlon21.reversedictionary.repository.data.ViewResource
import com.gscanlon21.reversedictionary.repository.data.map
import com.gscanlon21.reversedictionary.repository.search.result.SearchResultRepository
import com.gscanlon21.reversedictionary.ui.main.search.result.SearchResultItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest

@ExperimentalCoroutinesApi
class SearchResultViewModel constructor(application: Application, private val searchResultRepository: SearchResultRepository) : AndroidViewModel(application) {
    suspend fun resultList(type: ApiType, term: String): LiveData<ViewResource<List<SearchResultItem>?>> {
        return searchResultRepository.lookup(term, type).mapLatest { item ->
            when (item) {
                is ViewResource.WithData -> item.map { data ->
                    data.filter { i -> i.value.isNotBlank() && i.value.length > 1 }
                        .sortedByDescending { it.score }
                        .map { SearchResultItem(it) }
                        .filterNot {
                            if (getApplication<ReverseDictionaryApp>().defaultSharedPreferences().missingDefinitionsHidden(getApplication<ReverseDictionaryApp>())) {
                                // Keeping Anagrams since the aoi does not return definitions if they are not already cached
                                it.definition == null && type != ApiType.Datamuse.Definition && type != ApiType.Anagramica.Anagram
                            } else { false }
                        }.nullIfEmpty()
                }
                is ViewResource.Error -> item
            }
        }.asLiveData()
    }
}
