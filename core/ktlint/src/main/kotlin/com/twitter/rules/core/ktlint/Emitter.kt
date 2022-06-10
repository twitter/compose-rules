package com.twitter.rules.core.ktlint

import com.twitter.rules.core.startOffsetFromName
import org.jetbrains.kotlin.com.intellij.psi.PsiNameIdentifierOwner

fun interface Emitter {
    fun report(offset: Int, errorMessage: String, canBeAutoCorrected: Boolean)
}

fun Emitter.report(element: PsiNameIdentifierOwner, errorMessage: String, canBeAutoCorrected: Boolean = false) {
    report(element.startOffsetFromName, errorMessage, canBeAutoCorrected)
}
