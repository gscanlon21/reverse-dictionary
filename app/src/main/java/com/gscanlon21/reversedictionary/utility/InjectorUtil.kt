package com.gscanlon21.reversedictionary.utility

import android.content.Context
import com.gscanlon21.reversedictionary.db.SearchDb
import com.gscanlon21.reversedictionary.repository.history.HistoryRepository
import com.gscanlon21.reversedictionary.repository.search.SearchRepository
import com.gscanlon21.reversedictionary.repository.search.result.SearchResultRepository
import com.gscanlon21.reversedictionary.service.SearchResultService
import com.gscanlon21.reversedictionary.service.SearchService
import com.gscanlon21.reversedictionary.service.api.Requests
import com.gscanlon21.reversedictionary.vm.history.HistoryViewModelFactory
import com.gscanlon21.reversedictionary.vm.search.SearchTermViewModelFactory
import com.gscanlon21.reversedictionary.vm.search.SearchViewModelFactory
import com.gscanlon21.reversedictionary.vm.search.result.MetaViewModelFactory
import com.gscanlon21.reversedictionary.vm.search.result.SearchResultViewModelFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * Static methods used to inject classes needed for various Activities and Fragments
 */
@ExperimentalCoroutinesApi
object InjectorUtil {
    private fun getHistoryRepository(context: Context): HistoryRepository {
        return HistoryRepository(SearchDb.getInstance(context.applicationContext).history())
    }

    private fun getSearchRepository(context: Context): SearchRepository {
        return SearchRepository(
            SearchService(Requests.getInstance(context.applicationContext)), SearchDb.getInstance(context.applicationContext).search())
    }

    private fun getSearchResultRepository(context: Context): SearchResultRepository {
        return SearchResultRepository(
            SearchResultService(Requests.getInstance(context.applicationContext)), SearchDb.getInstance(context.applicationContext).searchResults())
    }

    fun provideHistoryViewModelFactory(context: Context): HistoryViewModelFactory {
        val repository = getHistoryRepository(context)
        return HistoryViewModelFactory(repository)
    }

    fun provideSearchTermViewModelFactory(context: Context): SearchTermViewModelFactory {
        val repository = getHistoryRepository(context)
        return SearchTermViewModelFactory(repository)
    }

    fun provideSearchViewModelFactory(context: Context): SearchViewModelFactory {
        val repository = getSearchRepository(context)
        return SearchViewModelFactory(repository)
    }

    fun provideSearchResultViewModelFactory(context: Context): SearchResultViewModelFactory {
        val repository = getSearchResultRepository(context)
        return SearchResultViewModelFactory(
            repository
        )
    }

    fun provideMetaViewModelFactory(context: Context): MetaViewModelFactory {
        val repository = getSearchResultRepository(context)
        return MetaViewModelFactory(repository)
    }
}
