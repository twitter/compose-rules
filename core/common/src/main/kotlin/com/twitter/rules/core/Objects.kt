package com.twitter.rules.core

/**
 * An unsafe cast into an arbitrary type [T].
 */
@Suppress("UNCHECKED_CAST")
fun <T> Any?.cast() = this as T
