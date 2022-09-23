// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules

import com.twitter.rules.core.ComposeKtConfig.Companion.config
import com.twitter.rules.core.ComposeKtVisitor
import com.twitter.rules.core.Emitter
import com.twitter.rules.core.report
import com.twitter.rules.core.util.declaresCompositionLocal
import com.twitter.rules.core.util.findChildrenByClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtProperty

class ComposeCompositionLocalAllowlist : ComposeKtVisitor {

    override fun visitFile(file: KtFile, autoCorrect: Boolean, emitter: Emitter) {
        val compositionLocals = file.findChildrenByClass<KtProperty>()
            .filter { it.declaresCompositionLocal }

        if (compositionLocals.none()) return

        val allowed = file.config().getSet("allowedCompositionLocals", emptySet())
        val notAllowed = compositionLocals.filterNot { allowed.contains(it.nameIdentifier?.text) }

        for (compositionLocal in notAllowed) {
            emitter.report(compositionLocal, CompositionLocalNotInAllowlist)
        }
    }

    companion object {
        val CompositionLocalNotInAllowlist = """
            CompositionLocals are implicit dependencies and creating new ones should be avoided.

            See https://twitter.github.io/compose-rules/rules/#make-dependencies-explicit for more information.
        """.trimIndent()
    }
}
