package com.gscanlon21.reversedictionary.service.api

data class WordnikAudioModel(
    val audioUrl: String,
    override val attributionText: String,
    override val attributionUrl: String
) : WordnikAttribution {
    companion object {
        const val AUDIO_URL_KEY = "fileUrl"
    }
}
