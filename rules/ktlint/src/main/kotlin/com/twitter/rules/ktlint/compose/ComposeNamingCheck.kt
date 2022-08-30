// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.rules.ktlint.compose

import com.twitter.rules.core.Emitter
import com.twitter.rules.core.ktlint.TwitterKtlintRule
import com.twitter.rules.core.report
import com.twitter.rules.core.util.returnsValue
import org.jetbrains.kotlin.psi.KtFunction

class ComposeNamingCheck : TwitterKtlintRule("twitter-compose:naming-check") {

    override fun visitComposable(function: KtFunction, autoCorrect: Boolean, emitter: Emitter) {
        // If it's a block we can't know if there is a return type or not from ktlint
        if (!function.hasBlockBody()) return
        val firstLetter = function.name?.first() ?: return

        if (function.returnsValue) {
            // If it returns value, the composable should start with a lowercase letter
            if (firstLetter.isUpperCase()) {
                emitter.report(function, ComposablesThatReturnResultsShouldBeLowercase)
            }
        } else {
            // If it returns Unit or doesn't have a return type, we should start with an uppercase letter
            if (firstLetter.isLowerCase()) {
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
