package com.gscanlon21.reversedictionary.service.api

data class DatamuseModel(val word: String, val score: Int, val defs: List<String>? = null) {
    companion object {
        const val WORD_KEY = "word"
        const val SCORE_KEY = "score"
        const val DEFS_KEY = "defs"
    }
}
