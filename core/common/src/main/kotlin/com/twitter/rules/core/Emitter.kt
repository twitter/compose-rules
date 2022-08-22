// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.rules.core

import com.twitter.rules.core.util.startOffsetFromName
import org.jetbrains.kotlin.com.intellij.psi.PsiNameIdentifierOwner

fun interface Emitter {
    fun report(offset: Int, errorMessage: String, canBeAutoCorrected: Boolean)
}

fun Emitter.report(element: PsiNameIdentifierOwner, errorMessage: String, canBeAutoCorrected: Boolean = false) {
    report(element.startOffsetFromName, errorMessage, canBeAutoCorrected)
}
