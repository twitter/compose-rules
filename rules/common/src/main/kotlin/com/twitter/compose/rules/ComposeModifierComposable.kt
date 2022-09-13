// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules

import com.twitter.rules.core.ComposeKtVisitor
import com.twitter.rules.core.Emitter
import com.twitter.rules.core.report
import com.twitter.rules.core.util.isModifierReceiver
import org.jetbrains.kotlin.psi.KtFunction

class ComposeModifierComposable : ComposeKtVisitor {

    override fun visitComposable(function: KtFunction, autoCorrect: Boolean, emitter: Emitter) {
        if (!function.isModifierReceiver) return

        emitter.report(function, ComposableModifier)
    }

    companion object {
        val ComposableModifier = """
            Using @Composable builder functions for modifiers is not recommended, as they cause unnecessary recompositions.
            You should use Modifier.composed { ... } instead, as it limits recomposition to just the modifier instance, rather than the whole function tree.

            See https://twitter.github.io/compose-rules/rules/#avoid-modifier-extension-factory-functions for more information.
        """.trimIndent()
    }
}
