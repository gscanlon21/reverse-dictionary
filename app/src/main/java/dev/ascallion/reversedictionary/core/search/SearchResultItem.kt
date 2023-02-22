package dev.ascallion.reversedictionary.core.search

import dev.ascallion.reversedictionary.R
import dev.ascallion.reversedictionary.core.repository.ApiType
import dev.ascallion.reversedictionary.db.search.SearchResultEntity

class SearchResultItem constructor(
    val title: String,
    val description: String? = null,
    val viewType: Int = R.layout.adapter_search_result
) {
    constructor(entity: SearchResultEntity) : this(
        entity.value,
        if (entity.extra.isNullOrEmpty()) { null } else { entity.extra.maxBy { it.score }.value },
        if (entity.type == ApiType.Datamuse.Definition) { R.layout.adapter_search_result } else { R.layout.adapter_search_result }
    )
}
