package com.gscanlon21.reversedictionary.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.NetworkError
import com.google.android.material.snackbar.Snackbar
import com.gscanlon21.reversedictionary.R
import com.gscanlon21.reversedictionary.adapter.search.SearchResultAdapter
import com.gscanlon21.reversedictionary.core.repository.ApiType
import com.gscanlon21.reversedictionary.core.repository.ViewResource
import com.gscanlon21.reversedictionary.core.search.SearchResultItem
import com.gscanlon21.reversedictionary.ui.MainActivity
import com.gscanlon21.reversedictionary.utility.InjectorUtil
import com.gscanlon21.reversedictionary.vm.search.SearchTermViewModel
import com.gscanlon21.reversedictionary.vm.search.SearchViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class SearchFragment : Fragment() {
    private lateinit var searchResultAdapter: SearchResultAdapter

    private val searchBarHandlers = SearchBarHandlers(this)

    private val searchTermViewModel: SearchTermViewModel by activityViewModels()
    private val searchViewModel: SearchViewModel by viewModels {
        InjectorUtil.provideSearchViewModelFactory(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_search, container, false)

        root.findViewById<RecyclerView>(R.id.search_list).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = SearchResultAdapter(searchViewModel.results).also { searchResultAdapter = it }
        }

        root.findViewById<Button>(R.id.wotd_button).apply {
            setOnClickListener { wotdButtonClickListener(it) }
        }

        root.findViewById<Button>(R.id.random_word_button).apply {
            setOnClickListener { randomWordClickListener(it) }
        }

        val searchBar = root.findViewById<SearchView>(R.id.search_bar).apply {
            setOnQueryTextListener(searchBarHandlers)
        }

        searchTermViewModel.searchPhrase.observe(viewLifecycleOwner, Observer { word ->
            searchBar.queryHint = word ?: getString(R.string.search_for_query_hint)
            if (word == null) { return@Observer }
            lifecycleScope.launch {
                searchViewModel.resultList(ApiType.Datamuse.MeansLike, word).observe(viewLifecycleOwner, resourceObserver)
            }
        })

        return root
    }

    private fun launchMainActivity(text: String) {
        val mainIntent = Intent(context, MainActivity::class.java).apply {
            putExtra(MainActivity.EXTRA_SEARCH_TERM, text)
        }
        startActivity(mainIntent)
    }

    private fun randomWordClickListener(view: View) = lifecycleScope.launch {
        searchViewModel.getRandomWord().observe(viewLifecycleOwner, Observer { resource ->
            when (resource) {
                is ViewResource.WithData.Success -> launchMainActivity(resource.data)
                is ViewResource.Error -> Snackbar.make(view, getString(R.string.placeholder_error), Snackbar.LENGTH_SHORT).show()
            }
        })
    }

    private fun wotdButtonClickListener(view: View) = lifecycleScope.launch {
        searchViewModel.getWordOfTheDay().observe(viewLifecycleOwner, Observer { resource ->
            when (resource) {
                is ViewResource.WithData.Success -> launchMainActivity(resource.data)
                is ViewResource.Error -> Snackbar.make(view, getString(R.string.placeholder_error), Snackbar.LENGTH_SHORT).show()
            }
        })
    }

    private class SearchBarHandlers(private val searchFragment: SearchFragment) : SearchView.OnQueryTextListener {
        override fun onQueryTextChange(newText: String): Boolean { return false }
        override fun onQueryTextSubmit(query: String): Boolean {
            searchFragment.launchMainActivity(query)
            return false
        }
    }

    private val resourceObserver = Observer<ViewResource<List<SearchResultItem>?>> { resource ->
        val results = when (resource) {
            is ViewResource.WithData.Success -> onSuccess(resource)
            is ViewResource.WithData.Loading -> onLoading(resource)
            is ViewResource.Error -> onError(resource)
        }

        setResults(results)
    }

    private fun setResults(results: List<SearchResultItem>) {
        searchViewModel.results.clear()
        searchViewModel.results.addAll(results)
        searchResultAdapter.notifyDataSetChanged()
    }

    private fun onSuccess(resource: ViewResource.WithData.Success<List<SearchResultItem>?>): List<SearchResultItem> {
        return resource.data ?: listOf(SearchResultItem(getString(R.string.placeholder_success)))
    }

    private fun onLoading(resource: ViewResource.WithData.Loading<List<SearchResultItem>?>): List<SearchResultItem> {
        return resource.data ?: listOf(SearchResultItem(getString(R.string.placeholder_loading)))
    }

    private fun onError(resource: ViewResource.Error): List<SearchResultItem> {
        if (resource.exception is NetworkError) {
            Snackbar.make(requireView(), getString(R.string.error_network_disconnected), Snackbar.LENGTH_SHORT).show()
        }

        return listOf(SearchResultItem(getString(R.string.placeholder_error)))
    }
}
