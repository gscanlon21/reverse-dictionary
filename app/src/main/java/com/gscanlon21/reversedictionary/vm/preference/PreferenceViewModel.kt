package com.gscanlon21.reversedictionary.vm.preference

import androidx.lifecycle.ViewModel
import com.gscanlon21.reversedictionary.R
import com.gscanlon21.reversedictionary.core.preference.Attribution

class PreferenceViewModel : ViewModel() {
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
