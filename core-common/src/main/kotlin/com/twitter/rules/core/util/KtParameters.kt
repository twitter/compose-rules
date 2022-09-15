// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.rules.core.util

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
        "MutableState<.*>\\??",
        // Flow
        "MutableStateFlow<.*>\\??",
        "MutableSharedFlow<.*>\\??",
    ).map { Regex(it) }
}
