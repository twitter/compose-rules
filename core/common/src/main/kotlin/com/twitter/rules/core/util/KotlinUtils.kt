package com.twitter.rules.core.util

fun <T> T.runIf(value: Boolean, block: T.() -> T): T =
    if (value) block() else this
