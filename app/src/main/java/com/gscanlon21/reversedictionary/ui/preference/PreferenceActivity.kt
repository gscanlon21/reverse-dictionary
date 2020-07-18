package com.gscanlon21.reversedictionary.ui.preference

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gscanlon21.reversedictionary.R
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class PreferenceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preference)

        supportFragmentManager.beginTransaction()
            .add(R.id.user_preference, UserPreferenceFragment())
            .commit()
    }
}
