package com.gscanlon21.reversedictionary.ui.main.search.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.gscanlon21.reversedictionary.R
import com.gscanlon21.reversedictionary.repository.data.ApiType
import com.gscanlon21.reversedictionary.repository.data.ViewResource
import com.gscanlon21.reversedictionary.ui.main.adapter.ListItemFragment
import com.gscanlon21.reversedictionary.ui.navigation.UiView
import com.gscanlon21.reversedictionary.utility.InjectorUtil
import com.gscanlon21.reversedictionary.vm.MainViewModel
import com.gscanlon21.reversedictionary.vm.search.SearchTermViewModel
import com.gscanlon21.reversedictionary.vm.search.result.SearchResultViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class SearchResultFragment : ListItemFragment<SearchResultItem>() {
    override val rootId = R.layout.fragment_search_result
    override val recyclerViewId = R.id.recycler_list

    private val searchTermViewModel: SearchTermViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private val searchResultViewModel: SearchResultViewModel by viewModels {
        InjectorUtil.provideSearchResultViewModelFactory(requireContext())
    }

    private val uiView: UiView by lazy {
        UiView.valueOf(arguments?.getString(EXTRA_TYPE) ?: UiView.None.name)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = super.onCreateView(inflater, container, savedInstanceState)!!
        if (uiView == UiView.None) { return root }

        searchTermViewModel.searchWord.observe(viewLifecycleOwner, Observer { word ->
            lifecycleScope.launch {
                ApiType.values().singleOrNull { it.name == uiView.name }?.let {
                    searchResultViewModel.resultList(it, word?.term!!).apply {
                        observe(viewLifecycleOwner, resourceObserver)
                        observe(viewLifecycleOwner, searchResultObserver)
                    }
                }
            }
        })

        if (uiView == UiView.SearchResult.Definition) {
            childFragmentManager.beginTransaction()
                .add(R.id.search_result_meta_frame, MetaFragment())
                .commitNow()
        }

        return root
    }

    private val searchResultObserver = Observer<ViewResource<List<SearchResultItem>>> { resource ->
        when (resource) {
            !is ViewResource.WithData.Loading -> {
                mainViewModel.loadingJobs[uiView]?.complete(resource)
            }
        }
    }

    companion object {
        const val EXTRA_TYPE = "EXTRA_TYPE"

        fun newInstance(type: UiView): SearchResultFragment {
            val args = Bundle()
            args.putString(EXTRA_TYPE, type.name)
            val frag = SearchResultFragment()
            frag.arguments = args
            return frag
        }
    }
}
