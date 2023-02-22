package dev.ascallion.reversedictionary.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.NetworkError
import com.google.android.material.snackbar.Snackbar
import dev.ascallion.reversedictionary.R
import dev.ascallion.reversedictionary.adapter.search.SearchResultAdapter
import dev.ascallion.reversedictionary.core.repository.ApiType
import dev.ascallion.reversedictionary.core.repository.ViewResource
import dev.ascallion.reversedictionary.core.search.SearchResultItem
import dev.ascallion.reversedictionary.core.ui.UiView
import dev.ascallion.reversedictionary.databinding.FragmentSearchResultBinding
import dev.ascallion.reversedictionary.databinding.RecyclerElevatedBinding
import dev.ascallion.reversedictionary.utility.InjectorUtil
import dev.ascallion.reversedictionary.vm.MainViewModel
import dev.ascallion.reversedictionary.vm.search.SearchTermViewModel
import dev.ascallion.reversedictionary.vm.search.SearchViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class SearchResultFragment : Fragment() {
    private lateinit var searchResultAdapter: SearchResultAdapter
    private lateinit var recyclerBinding: RecyclerElevatedBinding
    private lateinit var binding: FragmentSearchResultBinding

    private val searchTermViewModel: SearchTermViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private val searchViewModel: SearchViewModel by viewModels {
        InjectorUtil.provideSearchViewModelFactory(requireActivity().application)
    }

    private val uiView: UiView by lazy {
        UiView.valueOf(arguments?.getString(EXTRA_TYPE) ?: UiView.None.name)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSearchResultBinding.inflate(inflater, container, false)
        recyclerBinding = RecyclerElevatedBinding.bind(binding.root)

        if (uiView == UiView.None) { return binding.root }

        recyclerBinding.recyclerElevated.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = SearchResultAdapter(searchViewModel.results).also { searchResultAdapter = it }
        }

        if (uiView == UiView.SearchResult.Anagram) {
            lifecycleScope.launch {
                searchViewModel.findAnagrams(searchTermViewModel.searchWord.value!!).apply {
                    observe(viewLifecycleOwner, resourceObserver)
                    observe(viewLifecycleOwner, searchResultObserver)
                }
            }
        } else {
            lifecycleScope.launch {
                ApiType.values().singleOrNull { it.name == uiView.name }?.let {
                    searchViewModel.resultList(it, searchTermViewModel.searchWord.value!!).apply {
                        observe(viewLifecycleOwner, resourceObserver)
                        observe(viewLifecycleOwner, searchResultObserver)
                    }
                }
            }
        }

        return binding.root
    }

    private val searchResultObserver = Observer<ViewResource<List<SearchResultItem>?>> { resource ->
        when (resource) {
            !is ViewResource.WithData.Loading -> {
                mainViewModel.loadingJobs[uiView]?.complete(resource)
            }
            else -> Unit
        }
    }

    private val resourceObserver = Observer<ViewResource<List<SearchResultItem>?>> { resource ->
        // If the fragment was recreated and view model still has data, skip loading results
        if (resource is ViewResource.WithData.Loading && searchViewModel.results.isNotEmpty()) {
            return@Observer
        }

        val results = when (resource) {
            is ViewResource.WithData.Success -> onSuccess(resource)
            is ViewResource.WithData.Loading -> onLoading(resource)
            is ViewResource.Error -> onError(resource)
        }

        if (mainViewModel.viewPagerScrolling.value != true || resource !is ViewResource.WithData.Success) {
            setResults(results)
        } else {
            delayResultsWhileViewPagerScrolling(results)
        }
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

    private fun delayResultsWhileViewPagerScrolling(results: List<SearchResultItem>) {
        mainViewModel.viewPagerScrolling.observe(
            viewLifecycleOwner,
            object : Observer<Boolean> {
                override fun onChanged(t: Boolean?) {
                    if (t == false) {
                        setResults(results)
                        mainViewModel.viewPagerScrolling.removeObserver(this)
                    }
                }
            }
        )
    }

    companion object {
        const val EXTRA_TYPE = "EXTRA_TYPE"

        fun newInstance(type: UiView): SearchResultFragment {
            return SearchResultFragment().apply {
                arguments = Bundle().apply {
                    putString(EXTRA_TYPE, type.name)
                }
            }
        }
    }
}
