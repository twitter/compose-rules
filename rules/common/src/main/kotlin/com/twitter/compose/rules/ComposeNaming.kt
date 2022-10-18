// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules

import com.twitter.rules.core.ComposeKtConfig.Companion.config
import com.twitter.rules.core.ComposeKtVisitor
import com.twitter.rules.core.Emitter
import com.twitter.rules.core.report
import com.twitter.rules.core.util.hasReceiverType
import com.twitter.rules.core.util.returnsValue
import org.jetbrains.kotlin.psi.KtFunction

class ComposeNaming : ComposeKtVisitor {

    override fun visitComposable(function: KtFunction, autoCorrect: Boolean, emitter: Emitter) {
        // If it's a block we can't know if there is a return type or not from ktlint
        if (!function.hasBlockBody()) return
        val functionName = function.name?.takeUnless(String::isEmpty) ?: return
        val firstLetter = functionName.first()

        if (function.returnsValue) {
            // If it returns value, the composable should start with a lowercase letter
            if (firstLetter.isUpperCase()) {
                // If it's allowed, we don't report it
                val isAllowed = function.config().getSet("allowedNames", emptySet())
                    .any {
                        it.toRegex().matches(functionName)
                    }
                if (isAllowed) return
                emitter.report(function, ComposablesThatReturnResultsShouldBeLowercase)
            }
        } else {
            // If it returns Unit or doesn't have a return type, we should start with an uppercase letter
            // If the composable has a receiver, we can ignore this.
            if (firstLetter.isLowerCase() && !function.hasReceiverType) {
                emitter.report(function, ComposablesThatDoNotReturnResultsShouldBeCapitalized)
            }
        }
    }

    companion object {

        val ComposablesThatDoNotReturnResultsShouldBeCapitalized = """
            Composable functions that return Unit should start with an uppercase letter.
            They are considered declarative entities that can be either present or absent in a composition and therefore follow the naming rules for classes.

            See https://twitter.github.io/compose-rules/rules/#naming-composable-functions-properly for more information.
        """.trimIndent()

        val ComposablesThatReturnResultsShouldBeLowercase = """
            Composable functions that return a value should start with a lowercase letter.
            While useful and accepted outside of @Composable functions, this factory function convention has drawbacks that set inappropriate expectations for callers when used with @Composable functions.

            See https://twitter.github.io/compose-rules/rules/#naming-composable-functions-properly for more information.
        """.trimIndent()
    }
}
