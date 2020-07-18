package com.gscanlon21.reversedictionary.extension

import org.json.JSONArray

fun <T> JSONArray.toSequence(): Sequence<T> {
    return sequence {
        for (i in 0 until this@toSequence.length()) {
            @Suppress("UNCHECKED_CAST")
            yield(this@toSequence.get(i) as T)
        }
    }
}
