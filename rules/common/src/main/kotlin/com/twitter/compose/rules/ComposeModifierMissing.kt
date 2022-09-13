// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules

import com.twitter.rules.core.ComposeKtVisitor
import com.twitter.rules.core.Emitter
import com.twitter.rules.core.report
import com.twitter.rules.core.util.definedInInterface
import com.twitter.rules.core.util.emitsContent
import com.twitter.rules.core.util.isOverride
import com.twitter.rules.core.util.isPreview
import com.twitter.rules.core.util.modifierParameter
import com.twitter.rules.core.util.returnsValue
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.psiUtil.isPublic

class ComposeModifierMissing : ComposeKtVisitor {

    override fun visitComposable(function: KtFunction, autoCorrect: Boolean, emitter: Emitter) {
        // We want to find all composable functions that:
        //  - are public
        //  - emit content
        //  - are not overridden or part of an interface
        //  - are not a @Preview composable
        if (
            !function.isPublic ||
            function.returnsValue ||
            function.isOverride ||
            function.definedInInterface ||
            function.isPreview
        ) {
            return
        }

        // If there is a modifier param, we bail
        if (function.modifierParameter != null) return

        // In case we didn't find any `modifier` parameters, we check if it emits content and report the error if so.
        if (function.emitsContent) {
            emitter.report(function, MissingModifierContentComposable)
        }
    }

    companion object {
        val MissingModifierContentComposable = """
            This @Composable function emits content but doesn't have a modifier parameter.

            See https://twitter.github.io/compose-rules/rules/#when-should-i-expose-modifier-parameters for more information.
        """.trimIndent()
    }
}
