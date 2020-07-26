package com.gscanlon21.reversedictionary.ui.main.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.NetworkError
import com.google.android.material.snackbar.Snackbar
import com.gscanlon21.reversedictionary.R
import com.gscanlon21.reversedictionary.repository.data.ViewResource
import com.gscanlon21.reversedictionary.ui.main.MainFragment
import com.gscanlon21.reversedictionary.vm.MainViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
abstract class ListItemFragment<T : IListItem> : MainFragment() {
    private val mainViewModel: MainViewModel by activityViewModels()

    abstract val rootId: Int
    abstract val recyclerViewId: Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(rootId, container, false)

        val viewManager = LinearLayoutManager(context)
        listItemAdapter = ListItemAdapter(requireContext())
        root.findViewById<RecyclerView>(recyclerViewId).apply {
            layoutManager = viewManager
            adapter = listItemAdapter
        }

        return root
    }

    protected lateinit var listItemAdapter: ListItemAdapter<IListItem>
    protected val resourceObserver = Observer<ViewResource<List<T>?>> { resource ->
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

    private fun setResults(results: List<IListItem>) {
        listItemAdapter.results = results
        listItemAdapter.notifyDataSetChanged()
    }

    private fun onSuccess(resource: ViewResource.WithData.Success<List<T>?>): List<IListItem> {
        return resource.data ?: listOf(ListItem(getString(R.string.placeholder_success)))
    }

    private fun onLoading(resource: ViewResource.WithData.Loading<List<T>?>): List<IListItem> {
        return resource.data ?: listOf(ListItem(getString(R.string.placeholder_loading)))
    }

    private fun onError(resource: ViewResource.Error): List<IListItem> {
        if (resource.exception is NetworkError) {
            Snackbar.make(requireView(), getString(R.string.error_network_disconnected), Snackbar.LENGTH_SHORT).show()
        }

        return listOf(ListItem(getString(R.string.placeholder_error)))
    }

    private fun delayResultsWhileViewPagerScrolling(results: List<IListItem>) {
        mainViewModel.viewPagerScrolling.observe(
            viewLifecycleOwner, object : Observer<Boolean> {
            override fun onChanged(t: Boolean?) {
                if (t == false) {
                    setResults(results)
                    mainViewModel.viewPagerScrolling.removeObserver(this)
                }
            }
        })
    }
}
