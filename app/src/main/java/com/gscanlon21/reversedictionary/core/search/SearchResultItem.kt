package com.gscanlon21.reversedictionary.core.search

import com.gscanlon21.reversedictionary.R
import com.gscanlon21.reversedictionary.core.repository.ApiType
import com.gscanlon21.reversedictionary.db.search.SearchResultEntity

class SearchResultItem constructor(
    val title: String,
    val description: String? = null,
    val viewType: Int = R.layout.adapter_search_result
) {
    constructor(entity: SearchResultEntity) : this(
        entity.value,
        entity.extra?.maxBy { it.score }?.value,
        if (entity.type == ApiType.Datamuse.Definition) { R.layout.adapter_search_result } else { R.layout.adapter_search_result }
    )
}
