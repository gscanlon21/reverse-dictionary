package dev.ascallion.reversedictionary.core.repository

sealed class ApiType(val name: String) {
    object None : ApiType("None")

    /**
     * @property apiRoute The query string parameter for the Datamuse API
     */
    sealed class Datamuse(name: String, val apiRoute: String) : ApiType(name) {
        object Similar : Datamuse("Similar", "rel_trg=")
        object Rhyme : Datamuse("Rhyme", "rel_rhy=")
        object Antonym : Datamuse("Antonym", "rel_ant=")
        object Synonym : Datamuse("Synonym", "rel_syn=")
        object MeansLike : Datamuse("MeansLike", "ml=")
        object SoundsLike : Datamuse("SoundsLike", "sl=")
        object SpelledLike : Datamuse("SpelledLike", "sp=")
        object Adjective : Datamuse("Adjective", "rel_jjb=")
        object Noun : Datamuse("Noun", "rel_jja=")
        object Homophone : Datamuse("Homophone", "rel_hom=") // Sound-alike words
        object Predecessor : Datamuse("Predecessor", "rel_bgb=") // Word that immediately precedes another
        object Meronym : Datamuse("Meronym", "rel_par=") // Part of
        object Golonym : Datamuse("Golonym", "rel_com=") // Comprises
        object Follower : Datamuse("Follower", "rel_bga=") // Word that immediately follows another word
        object Hyponym : Datamuse("Hyponym", "rel_gen=") // More general than
        object Hypernym : Datamuse("Hypernym", "rel_gen=") // Kind of
        object Definition : Datamuse("Definition", "max=1&sp=")

        companion object {
            fun valueOf(name: String): Datamuse {
                return values().single { it.name == name }
            }

            fun values(): List<Datamuse> {
                return listOf(
                    Similar,
                    Rhyme,
                    Antonym,
                    Synonym,
                    MeansLike,
                    SoundsLike,
                    SpelledLike,
                    Adjective,
                    Noun,
                    Definition,
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

    companion object {
        fun valueOf(name: String): ApiType {
            return values().single { it.name == name }
        }

        fun values(): List<ApiType> {
            return Datamuse.values()
        }
    }
}
