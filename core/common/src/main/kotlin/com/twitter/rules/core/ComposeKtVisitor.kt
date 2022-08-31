// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.rules.core

import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunction

interface ComposeKtVisitor {
    fun visitFunction(function: KtFunction, autoCorrect: Boolean, emitter: Emitter) {}

    fun visitComposable(function: KtFunction, autoCorrect: Boolean, emitter: Emitter) {}

    fun visitClass(clazz: KtClass, autoCorrect: Boolean, emitter: Emitter) {}

    fun visitFile(file: KtFile, autoCorrect: Boolean, emitter: Emitter) {}
}
