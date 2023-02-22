package dev.ascallion.reversedictionary.ui.preference

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dev.ascallion.reversedictionary.R
import dev.ascallion.reversedictionary.databinding.ActivityPreferenceBinding
import dev.ascallion.reversedictionary.vm.preference.PreferenceViewModel

class PreferenceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPreferenceBinding

    private val preferenceViewModel: PreferenceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPreferenceBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
