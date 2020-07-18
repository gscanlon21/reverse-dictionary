package com.gscanlon21.reversedictionary.ui.main.adapter

import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
data class ListItem constructor(override val titleText: String) : IListItem {
    override val definition: String? = null
    override val pinned: Boolean? = null
    override val viewType = ListItemAdapter.VIEW_TYPE_NONE
}
