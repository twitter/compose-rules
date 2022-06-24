package com.twitter.rules.core

import org.jetbrains.kotlin.psi.KtParameter

val KtParameter.isTypeMutable: Boolean
    get() = typeReference?.text?.matchesAnyOf(KnownMutableCommonTypes) == true

private val KnownMutableCommonTypes by lazy {
    listOf(
        // Set
        "MutableSet<.*>\\??",
        "ArraySet<.*>\\??",
        "HashSet<.*>\\??",
        // List
        "MutableList<.*>\\??",
        "ArrayList<.*>\\??",
        // Array
        "SparseArray<.*>\\??",
        "SparseArrayCompat<.*>\\??",
        "LongSparseArray<.*>\\??",
        "SparseBooleanArray\\??",
        "SparseIntArray\\??",
        // Map
        "MutableMap<.*>\\??",
        "HashMap<.*>\\??",
        "Hashtable<.*>\\??",
        // Compose
        "MutableState<.*>\\??"
    ).map { Regex(it) }
}

val KtParameter.isModifier: Boolean
    get() = typeReference?.text == "Modifier"
