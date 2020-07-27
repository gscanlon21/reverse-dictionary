package com.gscanlon21.reversedictionary.service.api

interface WordnikAttribution {
    val attributionText: String
    val attributionUrl: String

    companion object {
        const val ATTRIBUTION_URL = "attributionUrl"
        const val ATTRIBUTION_TEXT = "attributionText"
    }
}
