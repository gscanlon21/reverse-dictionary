package dev.ascallion.reversedictionary.core.ui

import android.content.Context
import dev.ascallion.reversedictionary.R

sealed class UiView(val name: String, val resId: Int) {
    companion object {
        fun valueOf(name: String): UiView {
            return values().single { it.name == name }
        }

        fun values(): List<UiView> {
            return listOf(None)
                .plus(SearchResult.values())
                .plus(Main.values())
        }
    }

    object None : UiView("None", R.string.app_name)

    /**
     * Handles what ViewPager pages to show to the user for search results
     *
     * When adding a new page the following locations must be changed:
     * - [UiView.SearchResult] to handle the UI
     * - [ApiType] to handle the API
     * - [R.array.search_result_preference] to handle the user preference settings
     *
     * @param sortOrder
     * @param name a unique string value used to map to the API
     * @param resId the string resource
     */
    sealed class SearchResult(val sortOrder: Int, name: String, resId: Int) : UiView(name, resId) {
        object Anagram : SearchResult(5, "Anagram", R.string.search_result_tab_anagram)
        object Similar : SearchResult(6, "Similar", R.string.search_result_tab_similar)
        object Rhyme : SearchResult(4, "Rhyme", R.string.search_result_tab_rhyme)
        object Antonym : SearchResult(3, "Antonym", R.string.search_result_tab_antonym)
        object Synonym : SearchResult(2, "Synonym", R.string.search_result_tab_synonym)
        object MeansLike : SearchResult(7, "MeansLike", R.string.search_result_tab_meanslike)
        object SoundsLike : SearchResult(8, "SoundsLike", R.string.search_result_tab_soundslike)
        object SpelledLike : SearchResult(9, "SpelledLike", R.string.search_result_tab_spelledlike)
        object Adjective : SearchResult(10, "Adjective", R.string.search_result_tab_adjective)
        object Noun : SearchResult(11, "Noun", R.string.search_result_tab_noun)
        object Homophone : SearchResult(12, "Homophone", R.string.search_result_tab_homophone) // Sound-alike words
        object Predecessor : SearchResult(14, "Predecessor", R.string.search_result_tab_predecessor) // Word that immediately precedes another
        object Meronym : SearchResult(15, "Meronym", R.string.search_result_tab_meronym) // Part of
        object Golonym : SearchResult(16, "Golonym", R.string.search_result_tab_golonym) // Comprises
        object Follower : SearchResult(17, "Follower", R.string.search_result_tab_follower) // Word that immediately follows another word
        object Hyponym : SearchResult(18, "Hyponym", R.string.search_result_tab_hyponym) // More general than
        object Hypernym : SearchResult(19, "Hypernym", R.string.search_result_tab_hypernym) // Kind of
        object Definition : SearchResult(1, "Definition", R.string.search_result_tab_definition)

        companion object {
            fun valueOf(name: String): SearchResult {
                return values().single { it.name == name }
            }

            fun resValueOf(name: String, context: Context): SearchResult {
                return values().single { context.getString(it.resId) == name }
            }

            fun values(): List<SearchResult> {
                return listOf(
                    Anagram,
                    Antonym,
                    Definition,
                    Rhyme,
                    Similar,
                    Synonym,
                    MeansLike,
                    SoundsLike,
                    SpelledLike,
                    Adjective,
                    Noun,
                    Homophone,
                    Predecessor,
                    Meronym,
                    Golonym,
                    Follower,
                    Hyponym,
                    Hypernym
                )
            }
        }
    }

    sealed class Main(name: String, resId: Int) : UiView(name, resId) {
        object Search : Main("Search", R.string.main_tab_search)
        object History : Main("History", R.string.main_tab_history)

        companion object {
            fun valueOf(name: String): Main {
                return values().single { it.name == name }
            }

            fun values(): List<Main> {
                return listOf(History, Search)
            }
        }
    }
}
