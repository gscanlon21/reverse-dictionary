package com.gscanlon21.reversedictionary.vm.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.gscanlon21.reversedictionary.db.search.WordOfTheDayEntity
import com.gscanlon21.reversedictionary.repository.data.ViewResource
import com.gscanlon21.reversedictionary.repository.data.map
import com.gscanlon21.reversedictionary.repository.search.SearchRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map

@ExperimentalCoroutinesApi
class SearchViewModel constructor(private val searchRepository: SearchRepository) : ViewModel() {
    suspend fun wordOfTheDay(): LiveData<ViewResource<String>> = searchRepository.getWordOfTheDay().map { wotdEntity ->
        when (wotdEntity) {
            is ViewResource.WithData<WordOfTheDayEntity> -> wotdEntity.map { data -> data?.name }
            is ViewResource.Error -> wotdEntity
        }
    }.asLiveData()

    suspend fun randomWord(): LiveData<ViewResource<String>> = searchRepository.getRandomWord().asLiveData()
}
