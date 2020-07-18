package com.gscanlon21.reversedictionary.ui.main.adapter

interface IListItem {
    val titleText: String
    val definition: String?
    val viewType: Int
    val pinned: Boolean?
}
