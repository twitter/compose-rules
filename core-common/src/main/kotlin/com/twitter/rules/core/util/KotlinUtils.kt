// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.rules.core.util

import java.util.Locale

fun <T> T.runIf(value: Boolean, block: T.() -> T): T =
    if (value) block() else this

fun String?.matchesAnyOf(patterns: Sequence<Regex>): Boolean {
    if (isNullOrEmpty()) return false
    for (regex in patterns) {
        if (matches(regex)) return true
    }
    return false
}

fun String.toCamelCase() = split('_').joinToString(
    separator = "",
    transform = { original ->
        original.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    },
)

fun String.toSnakeCase() = replace(humps, "_").lowercase(Locale.getDefault())

private val humps by lazy(LazyThreadSafetyMode.NONE) { "(?<=.)(?=\\p{Upper})".toRegex() }
