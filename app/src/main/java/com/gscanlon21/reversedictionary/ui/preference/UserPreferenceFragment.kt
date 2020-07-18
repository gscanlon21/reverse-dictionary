package com.gscanlon21.reversedictionary.ui.preference

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.snackbar.Snackbar
import com.gscanlon21.reversedictionary.R
import java.util.HashSet
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class UserPreferenceFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.user_preference, rootKey)
    }

    override fun onDisplayPreferenceDialog(preference: Preference?) {
        super.onDisplayPreferenceDialog(preference)

        if (preference?.key == getString(R.string.key_shared_preference_pages_to_show)) {
            preference.setOnPreferenceChangeListener prefChange@{ _, newValue ->
                val isValid = (newValue as HashSet<*>).count() > 0
                if (!isValid) {
                    Snackbar.make(requireView(), getString(R.string.error_user_preference_invalid), Snackbar.LENGTH_SHORT).show()
                }
                return@prefChange isValid
            }
        }
    }
}
