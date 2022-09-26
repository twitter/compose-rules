// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules

import com.twitter.rules.core.ComposeKtVisitor
import com.twitter.rules.core.Emitter
import com.twitter.rules.core.report
import com.twitter.rules.core.util.declaresCompositionLocal
import com.twitter.rules.core.util.findChildrenByClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtProperty

class ComposeCompositionLocalNaming : ComposeKtVisitor {

    override fun visitFile(file: KtFile, autoCorrect: Boolean, emitter: Emitter) {
        val compositionLocals = file.findChildrenByClass<KtProperty>()
            .filter { it.declaresCompositionLocal }

        if (compositionLocals.none()) return

        val notAllowed = compositionLocals.filterNot { it.nameIdentifier?.text?.startsWith("Local") == true }

        for (compositionLocal in notAllowed) {
            emitter.report(compositionLocal, CompositionLocalNeedsLocalPrefix)
        }
    }

    companion object {
        val CompositionLocalNeedsLocalPrefix = """
            CompositionLocals should be named using the `Local` prefix as an adjective, followed by a descriptive noun.

            See https://twitter.github.io/compose-rules/rules/#naming-compositionlocals-properly for more information.
        """.trimIndent()
    }
}
