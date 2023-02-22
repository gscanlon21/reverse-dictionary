package dev.ascallion.reversedictionary

import android.app.Application
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate

@Suppress("unused")
class ReverseDictionaryApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_YES
            )
        }
    }
}
