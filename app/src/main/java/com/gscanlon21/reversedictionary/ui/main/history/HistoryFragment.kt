package com.gscanlon21.reversedictionary.ui.main.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.gscanlon21.reversedictionary.R
import com.gscanlon21.reversedictionary.ui.main.adapter.ListItemFragment
import com.gscanlon21.reversedictionary.utility.InjectorUtil
import com.gscanlon21.reversedictionary.vm.history.HistoryViewModel
import com.gscanlon21.reversedictionary.vm.search.SearchTermViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class HistoryFragment : ListItemFragment<HistoryItem>() {
    private val searchTermViewModel: SearchTermViewModel by activityViewModels()
    private val historyViewModel: HistoryViewModel by viewModels {
        InjectorUtil.provideHistoryViewModelFactory(requireContext())
    }

    override val rootId = R.layout.fragment_history
    override val recyclerViewId = R.id.recycler_list

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = super.onCreateView(inflater, container, savedInstanceState)!!

        lifecycleScope.launch {
            historyViewModel.historyList().observe(viewLifecycleOwner, resourceObserver)
        }

        listItemAdapter.setOnFavToggled { word, newState ->
            searchTermViewModel.toggleFavWord(word, newState)
        }

        return root
    }
}
