package com.gscanlon21.reversedictionary.service.api

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class Requests private constructor(context: Context) {
    companion object {
        @Volatile private var instance: Requests? = null
        fun getInstance(context: Context): Requests {
            return instance ?: synchronized(this) {
                instance ?: Requests(context).also { instance = it }
            }
        }
    }

    val queue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

    val context: Context = context.applicationContext
}
