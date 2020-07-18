package com.gscanlon21.reversedictionary.extension

import org.json.JSONObject

fun JSONObject.getString(name: String, default: String): String {
    return if (this.has(name)) {
        this.getString(name)
    } else {
        default
    }
}

fun <T> JSONObject.getSequenceOrNull(name: String): Sequence<T>? {
    return if (this.has(name)) {
        this.getJSONArray(name).toSequence()
    } else {
        null
    }
}

fun JSONObject.getInt(name: String, default: Int): Int {
    return if (this.has(name)) {
        this.getInt(name)
    } else {
        default
    }
}
