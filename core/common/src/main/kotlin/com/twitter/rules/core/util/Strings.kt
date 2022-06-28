package com.twitter.rules.core.util

fun String?.matchesAnyOf(patterns: Collection<Regex>): Boolean {
    if (!isNullOrEmpty()) {
        patterns.forEach { regex ->
            if (matches(regex)) return true
        }
    }
    return false
}
