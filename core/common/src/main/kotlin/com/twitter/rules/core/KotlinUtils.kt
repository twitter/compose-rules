package com.twitter.rules.core

fun <T> T.runIf(value: Boolean, block: T.() -> T): T =
    if (value) block() else this
