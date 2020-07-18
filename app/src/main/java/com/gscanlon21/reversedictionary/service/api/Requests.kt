package com.gscanlon21.reversedictionary.service.api

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class Requests constructor(context: Context) {
    val queue: RequestQueue by lazy {
        // applicationContext is key, it keeps you from leaking the
        // ... Activity or BroadcastReceiver if someone passes one in
        Volley.newRequestQueue(context.applicationContext)
    }

    val context: Context = context.applicationContext
}
