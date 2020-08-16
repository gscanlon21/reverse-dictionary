package com.gscanlon21.reversedictionary.utility

import android.app.Application
import android.content.Context
import com.gscanlon21.reversedictionary.db.SearchDb
import com.gscanlon21.reversedictionary.repository.history.HistoryRepository
import com.gscanlon21.reversedictionary.repository.history.HistoryRepositoryImpl
import com.gscanlon21.reversedictionary.repository.search.SearchRepository
import com.gscanlon21.reversedictionary.repository.search.SearchRepositoryImpl
import com.gscanlon21.reversedictionary.service.SearchServiceImpl
import com.gscanlon21.reversedictionary.service.api.Requests
import com.gscanlon21.reversedictionary.vm.history.HistoryViewModelFactory
import com.gscanlon21.reversedictionary.vm.search.SearchTermViewModelFactory
import com.gscanlon21.reversedictionary.vm.search.SearchViewModelFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * Static methods used to inject classes needed for various Activities and Fragments
 */
@ExperimentalCoroutinesApi
object InjectorUtil {
    private fun getHistoryRepository(context: Context): HistoryRepository {
        return HistoryRepositoryImpl.getInstance(SearchDb.getInstance(context.applicationContext).history())
    }

    private fun getSearchRepository(context: Context): SearchRepository {
        return SearchRepositoryImpl.getInstance(
            context.applicationContext,
            SearchServiceImpl(Requests.getInstance(context.applicationContext)),
            SearchDb.getInstance(context.applicationContext).search()
        )
    }

    fun provideHistoryViewModelFactory(context: Context): HistoryViewModelFactory {
        val repository = getHistoryRepository(context)
        return HistoryViewModelFactory(repository)
    }

    fun provideSearchTermViewModelFactory(context: Context): SearchTermViewModelFactory {
        val repository = getHistoryRepository(context)
        return SearchTermViewModelFactory(repository)
    }

    fun provideSearchViewModelFactory(application: Application): SearchViewModelFactory {
        val repository = getSearchRepository(application)
        return SearchViewModelFactory(application, repository)
    }
}
