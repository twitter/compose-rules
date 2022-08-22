// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.rules.core.util

fun <T> T.runIf(value: Boolean, block: T.() -> T): T =
    if (value) block() else this

fun String?.matchesAnyOf(patterns: Collection<Regex>): Boolean {
    if (!isNullOrEmpty()) {
        patterns.forEach { regex ->
            if (matches(regex)) return true
        }
    }
    return false
}
