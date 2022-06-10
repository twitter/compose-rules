package com.twitter.rules.core

import org.jetbrains.kotlin.psi.KtFunction

val KtFunction.returnsValue: Boolean
    get() = typeReference != null && typeReference!!.text != "Unit"
