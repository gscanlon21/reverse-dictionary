package com.gscanlon21.reversedictionary.ui.preference

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.gscanlon21.reversedictionary.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RawTextActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_TEXT = "EXTRA_TEXT"
        const val EXTRA_RAW_RESOURCE = "EXTRA_RAW_RESOURCE"
        const val EXTRA_TITLE = "EXTRA_TITLE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_raw_text)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(intent.getIntExtra(EXTRA_TITLE, R.string.empty))

        lifecycleScope.launchWhenCreated {
            val resId = intent.getIntExtra(EXTRA_RAW_RESOURCE, 0)
            findViewById<TextView>(R.id.raw_text).apply {
                movementMethod = ScrollingMovementMethod()
                text = if (resId != 0) { withContext(Dispatchers.IO) {
                    applicationContext.resources.openRawResource(resId).bufferedReader(Charsets.UTF_8).readText()
                } } else {
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
