// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.rules.core.util

import org.jetbrains.kotlin.psi.KtParameter

val KtParameter.isTypeMutable: Boolean
    get() = typeReference?.text?.matchesAnyOf(KnownMutableCommonTypes) == true

private val KnownMutableCommonTypes = sequenceOf(
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
    "SnapshotStateList<.*>\\??",
    // Flow
    "MutableStateFlow<.*>\\??",
    "MutableSharedFlow<.*>\\??",
    // RxJava & RxRelay
    "PublishSubject<.*>\\??",
    "BehaviorSubject<.*>\\??",
    "ReplaySubject<.*>\\??",
    "PublishRelay<.*>\\??",
    "BehaviorRelay<.*>\\??",
    "ReplayRelay<.*>\\??"
).map { Regex(it) }
