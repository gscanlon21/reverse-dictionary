package com.gscanlon21.reversedictionary.extension

fun <E : Any, T : Collection<E>> T?.nullIfEmpty(): T? {
    if (this.isNullOrEmpty()) { return null }
    return this
}
