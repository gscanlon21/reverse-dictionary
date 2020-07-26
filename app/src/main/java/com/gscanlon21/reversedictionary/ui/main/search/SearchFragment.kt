package com.gscanlon21.reversedictionary.ui.main.search

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.gscanlon21.reversedictionary.R
import com.gscanlon21.reversedictionary.repository.data.ApiType
import com.gscanlon21.reversedictionary.repository.data.ViewResource
import com.gscanlon21.reversedictionary.ui.main.MainActivity
import com.gscanlon21.reversedictionary.ui.main.adapter.ListItemFragment
import com.gscanlon21.reversedictionary.ui.main.search.result.SearchResultItem
import com.gscanlon21.reversedictionary.utility.InjectorUtil
import com.gscanlon21.reversedictionary.vm.search.SearchTermViewModel
import com.gscanlon21.reversedictionary.vm.search.SearchViewModel
import com.gscanlon21.reversedictionary.vm.search.result.SearchResultViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class SearchFragment : ListItemFragment<SearchResultItem>() {
    override val rootId = R.layout.fragment_search
    override val recyclerViewId = R.id.search_list

    private val searchTermViewModel: SearchTermViewModel by activityViewModels()
    private val searchViewModel: SearchViewModel by viewModels {
        InjectorUtil.provideSearchViewModelFactory(requireContext())
    }
    private val searchResultViewModel: SearchResultViewModel by viewModels {
        InjectorUtil.provideSearchResultViewModelFactory(requireContext())
    }

    private val searchBarHandlers = SearchBarHandlers(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = super.onCreateView(inflater, container, savedInstanceState)!!

        val wotdButton: Button = root.findViewById(R.id.wotd_button)
        val randomWordButton: Button = root.findViewById(R.id.random_word_button)
        val searchBar: SearchView = root.findViewById(R.id.search_bar)

        randomWordButton.setOnClickListener { randomWordClickListener(it) }
        wotdButton.setOnClickListener { wotdButtonClickListener(it) }
        searchBar.setOnQueryTextListener(searchBarHandlers)

        searchTermViewModel.searchPhrase.observe(viewLifecycleOwner, Observer { word ->
            searchBar.queryHint = word ?: getString(R.string.search_for_query_hint)
            if (word == null) { return@Observer }
            lifecycleScope.launch {
                searchResultViewModel.resultList(ApiType.Datamuse.MeansLike, word).observe(viewLifecycleOwner, resourceObserver)
            }
        })

        return root
    }

    private fun launchMainActivity(text: String) {
        val i = Intent(context, MainActivity::class.java)
        i.putExtra(MainActivity.EXTRA_SEARCH_TERM, text)
        startActivity(i)
    }

    private fun randomWordClickListener(view: View) = lifecycleScope.launch {
        searchViewModel.randomWord().observe(viewLifecycleOwner, Observer { resource ->
            when (resource) {
                is ViewResource.WithData.Success -> launchMainActivity(resource.data)
                is ViewResource.Error -> Snackbar.make(view, getString(R.string.placeholder_error), Snackbar.LENGTH_SHORT).show()
            }
        })
    }

    private fun wotdButtonClickListener(view: View) = lifecycleScope.launch {
        searchViewModel.wordOfTheDay().observe(viewLifecycleOwner, Observer { resource ->
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
}
