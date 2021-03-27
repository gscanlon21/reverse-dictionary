package com.gscanlon21.reversedictionary.ui.preference

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.gscanlon21.reversedictionary.R
import com.gscanlon21.reversedictionary.databinding.ActivityRawTextBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RawTextActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRawTextBinding

    companion object {
        const val EXTRA_TEXT = "EXTRA_TEXT"
        const val EXTRA_RAW_RESOURCE = "EXTRA_RAW_RESOURCE"
        const val EXTRA_TITLE = "EXTRA_TITLE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRawTextBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(intent.getIntExtra(EXTRA_TITLE, R.string.empty))

        lifecycleScope.launchWhenCreated {
            val resId = intent.getIntExtra(EXTRA_RAW_RESOURCE, 0)
            binding.rawText.apply {
                movementMethod = ScrollingMovementMethod()
                text = if (resId != 0) {
                    withContext(Dispatchers.IO) {
                        applicationContext.resources.openRawResource(resId).bufferedReader(Charsets.UTF_8).readText()
                    }
                } else {
                    intent.getStringExtra(EXTRA_TEXT)
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
