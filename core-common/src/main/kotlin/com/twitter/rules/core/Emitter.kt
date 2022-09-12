// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.rules.core

import org.jetbrains.kotlin.com.intellij.psi.PsiElement

fun interface Emitter {
    fun report(element: PsiElement, errorMessage: String, canBeAutoCorrected: Boolean)
}

fun Emitter.report(element: PsiElement, errorMessage: String) {
    report(element = element, errorMessage = errorMessage, canBeAutoCorrected = false)
}
