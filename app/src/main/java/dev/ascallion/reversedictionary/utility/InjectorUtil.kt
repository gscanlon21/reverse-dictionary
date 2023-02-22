package dev.ascallion.reversedictionary.utility

import android.app.Application
import android.content.Context
import dev.ascallion.reversedictionary.db.SearchDb
import dev.ascallion.reversedictionary.repository.history.HistoryRepository
import dev.ascallion.reversedictionary.repository.history.HistoryRepositoryImpl
import dev.ascallion.reversedictionary.repository.search.SearchRepository
import dev.ascallion.reversedictionary.repository.search.SearchRepositoryImpl
import dev.ascallion.reversedictionary.service.SearchServiceImpl
import dev.ascallion.reversedictionary.service.api.Requests
import dev.ascallion.reversedictionary.vm.history.HistoryViewModelFactory
import dev.ascallion.reversedictionary.vm.search.SearchTermViewModelFactory
import dev.ascallion.reversedictionary.vm.search.SearchViewModelFactory
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
