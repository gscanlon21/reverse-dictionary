package com.gscanlon21.reversedictionary.ui.preference

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.snackbar.Snackbar
import com.gscanlon21.reversedictionary.R
import java.util.HashSet

class PreferenceFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference, rootKey)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        return when (preference?.key) {
            getString(R.string.key_shared_preference_source_code) -> {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(SOURCE_CODE_URL)))
                true
            }
            else -> super.onPreferenceTreeClick(preference)
        }
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

    companion object {
        const val SOURCE_CODE_URL = "https://github.com/gscanlon21/reversedictionary"
    }
}
