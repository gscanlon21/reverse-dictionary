package com.gscanlon21.reversedictionary.vm.preference

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gscanlon21.reversedictionary.R
import com.gscanlon21.reversedictionary.core.preference.Attribution

class PreferenceViewModel : ViewModel() {
    private val _title: MutableLiveData<Int?> = MutableLiveData()
    val title: LiveData<Int?> get() = _title

    fun setTitle(new: Int) {
        _title.value = new
    }

    val attributions = listOf(
        Attribution(
            R.string.attribution_scowl,
            license = R.raw.scowl,
            link = null
        ),
        Attribution(
            R.string.attribution_material_icons,
            license = R.raw.material_icons,
            link = null
        ),
        Attribution(
            R.string.attribution_datamuse,
            license = null,
            link = R.string.attribution_datamuse_link
        )
    )
}
