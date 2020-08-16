package com.gscanlon21.reversedictionary.ui.preference

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gscanlon21.reversedictionary.R
import com.gscanlon21.reversedictionary.adapter.preference.AttributionAdapter
import com.gscanlon21.reversedictionary.vm.preference.PreferenceViewModel

class AttributionFragment : Fragment() {
    private val preferenceViewModel: PreferenceViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_attribution, container, false)

        root.findViewById<RecyclerView>(R.id.license_licenses).apply {
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            layoutManager = LinearLayoutManager(context)
            adapter = AttributionAdapter(preferenceViewModel.attributions)
        }

        return root
    }

    override fun onResume() {
        super.onResume()

        preferenceViewModel.setTitle(R.string.preference_title_attribution)
    }
}
