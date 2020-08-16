package com.gscanlon21.reversedictionary.ui.history

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
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.NetworkError
import com.google.android.material.snackbar.Snackbar
import com.gscanlon21.reversedictionary.R
import com.gscanlon21.reversedictionary.adapter.history.HistoryAdapter
import com.gscanlon21.reversedictionary.core.history.HistoryItem
import com.gscanlon21.reversedictionary.core.repository.ViewResource
import com.gscanlon21.reversedictionary.utility.InjectorUtil
import com.gscanlon21.reversedictionary.vm.history.HistoryViewModel
import com.gscanlon21.reversedictionary.vm.search.SearchTermViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class HistoryFragment : Fragment() {
    private lateinit var historyAdapter: HistoryAdapter

    private val searchTermViewModel: SearchTermViewModel by activityViewModels()
    private val historyViewModel: HistoryViewModel by viewModels {
        InjectorUtil.provideHistoryViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_history, container, false)

        historyAdapter = HistoryAdapter(historyViewModel.historyItems).apply {
            setOnFavToggled { word, newState ->
                searchTermViewModel.toggleFavWord(word, newState)
            }
        }

        root.findViewById<RecyclerView>(R.id.recycler_elevated).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = historyAdapter
        }

        lifecycleScope.launch {
            historyViewModel.historyList().observe(viewLifecycleOwner, resourceObserver)
        }

        return root
    }

    private val resourceObserver = Observer<ViewResource<List<HistoryItem>?>> { resource ->
        val results = when (resource) {
            is ViewResource.WithData.Success -> onSuccess(resource)
            is ViewResource.WithData.Loading -> onLoading(resource)
            is ViewResource.Error -> onError(resource)
        }

        setResults(results)
    }

    private fun setResults(results: List<HistoryItem>) {
        historyViewModel.historyItems.clear()
        historyViewModel.historyItems.addAll(results.distinctBy { it.title })
        historyAdapter.notifyDataSetChanged()
    }

    private fun onSuccess(resource: ViewResource.WithData.Success<List<HistoryItem>?>): List<HistoryItem> {
        return resource.data ?: listOf(HistoryItem(getString(R.string.placeholder_success)))
    }

    private fun onLoading(resource: ViewResource.WithData.Loading<List<HistoryItem>?>): List<HistoryItem> {
        return resource.data ?: listOf(HistoryItem(getString(R.string.placeholder_loading)))
    }

    private fun onError(resource: ViewResource.Error): List<HistoryItem> {
        if (resource.exception is NetworkError) {
            Snackbar.make(requireView(), getString(R.string.error_network_disconnected), Snackbar.LENGTH_SHORT).show()
        }

        return listOf(HistoryItem(getString(R.string.placeholder_error)))
    }
}
