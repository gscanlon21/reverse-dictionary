package com.gscanlon21.reversedictionary.ui.preference

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import com.gscanlon21.reversedictionary.R
import com.gscanlon21.reversedictionary.vm.preference.PreferenceViewModel

class PreferenceActivity : AppCompatActivity() {
    private val preferenceViewModel: PreferenceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preference)

        supportFragmentManager.beginTransaction()
            .add(R.id.user_preference, PreferenceFragment())
            .commit()
    }

    override fun onResume() {
        super.onResume()

        preferenceViewModel.title.observe(this) {
            if (it != null) {
                supportActionBar?.title = getString(it)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            finish()
        }
        return true
    }
}
