package com.twitter.rules.ktlint.compose

import com.twitter.rules.core.isComposable
import com.twitter.rules.core.ktlint.Emitter
import com.twitter.rules.core.ktlint.TwitterKtRule
import com.twitter.rules.core.ktlint.report
import com.twitter.rules.core.returnsValue
import org.jetbrains.kotlin.psi.KtFunction

class ComposeNamingCheck : TwitterKtRule("compose-naming-check") {

    override fun visitFunction(function: KtFunction, autoCorrect: Boolean, emitter: Emitter) {
        if (!function.isComposable) return

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

            For more information: https://github.com/twitter/compose-ktlint-rules/blob/main/docs/rules.md#naming-composable-functions-properly
        """.trimIndent()

        val ComposablesThatReturnResultsShouldBeLowercase = """
            Composable functions that return a value should start with a lowercase letter.
            While useful and accepted outside of @Composable functions, this factory function convention has drawbacks that set inappropriate expectations for callers when used with @Composable functions.

            For more information: https://github.com/twitter/compose-ktlint-rules/blob/main/docs/rules.md#naming-composable-functions-properly
        """.trimIndent()
    }
}
