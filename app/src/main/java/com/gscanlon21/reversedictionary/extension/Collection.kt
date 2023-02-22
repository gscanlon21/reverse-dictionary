package dev.ascallion.reversedictionary.extension

fun <E : Any, T : Collection<E>> T?.nullIfEmpty(): T? {
    if (this.isNullOrEmpty()) { return null }
    return this
}
