package dev.ascallion.reversedictionary.vm.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import dev.ascallion.reversedictionary.ReverseDictionaryApp
import dev.ascallion.reversedictionary.core.repository.ApiType
import dev.ascallion.reversedictionary.core.repository.ViewResource
import dev.ascallion.reversedictionary.core.repository.map
import dev.ascallion.reversedictionary.core.search.SearchResultItem
import dev.ascallion.reversedictionary.extension.defaultSharedPreferences
import dev.ascallion.reversedictionary.extension.missingDefinitionsHidden
import dev.ascallion.reversedictionary.extension.nullIfEmpty
import dev.ascallion.reversedictionary.repository.search.SearchRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest

@ExperimentalCoroutinesApi
class SearchViewModel constructor(
    application: Application,
    private val searchRepository: SearchRepository
) : AndroidViewModel(application) {

    val results: ArrayList<SearchResultItem> = arrayListOf()

    suspend fun resultList(type: ApiType, term: String): LiveData<ViewResource<List<SearchResultItem>?>> {
        return searchRepository.lookup(term, type).mapLatest { item ->
            when (item) {
                is ViewResource.WithData -> item.map { data ->
                    data.filter { i -> i.value.isNotBlank() && i.value.length > 1 }
                        .sortedByDescending { it.score }
                        .map { SearchResultItem(it) }
                        .filterNot {
                            if (getApplication<ReverseDictionaryApp>().defaultSharedPreferences().missingDefinitionsHidden(getApplication<ReverseDictionaryApp>())) {
                                it.description == null && type != ApiType.Datamuse.Definition
                            } else { false }
                        }.nullIfEmpty()
                }
                is ViewResource.Error -> item
            }
        }.asLiveData()
    }

    suspend fun findAnagrams(word: String): LiveData<ViewResource<List<SearchResultItem>?>> {
        return searchRepository.getAnagrams(word).mapLatest { res ->
            when (res) {
                is ViewResource.WithData -> res.map { lst ->
                    lst.map { SearchResultItem(it) }.nullIfEmpty()
                }
                is ViewResource.Error -> res
            }
        }.asLiveData()
    }

    suspend fun getWordOfTheDay(): LiveData<ViewResource<String>> {
        return searchRepository.getWordOfTheDay().mapLatest { res ->
            when (res) {
                is ViewResource.WithData -> res.map { it.name }
                is ViewResource.Error -> res
            }
        }.asLiveData()
    }

    suspend fun getRandomWord(): LiveData<ViewResource<String>> {
        return searchRepository.getRandomWord().asLiveData()
    }
}
