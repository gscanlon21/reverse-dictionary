package dev.ascallion.reversedictionary.service.api

import android.annotation.SuppressLint
import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class Requests private constructor(context: Context) {
    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile private var instance: Requests? = null
        fun getInstance(context: Context): Requests {
            return instance ?: synchronized(this) {
                instance ?: Requests(context).also { instance = it }
            }
        }
    }

    val queue: RequestQueue by lazy {
        // Using applicationContext to avoid any potential memory leak
        Volley.newRequestQueue(context.applicationContext)
    }

    val context: Context = context.applicationContext
}
