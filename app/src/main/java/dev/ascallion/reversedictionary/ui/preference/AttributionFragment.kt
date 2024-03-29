package dev.ascallion.reversedictionary.ui.preference

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dev.ascallion.reversedictionary.R
import dev.ascallion.reversedictionary.adapter.preference.AttributionAdapter
import dev.ascallion.reversedictionary.databinding.FragmentAttributionBinding
import dev.ascallion.reversedictionary.vm.preference.PreferenceViewModel

class AttributionFragment : Fragment() {
    private lateinit var binding: FragmentAttributionBinding
    private val preferenceViewModel: PreferenceViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAttributionBinding.inflate(inflater, container, false)

        binding.licenseLicenses.apply {
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            layoutManager = LinearLayoutManager(context)
            adapter = AttributionAdapter(preferenceViewModel.attributions)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        preferenceViewModel.setTitle(R.string.preference_title_attribution)
    }
}
