// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules

import com.twitter.rules.core.ComposeKtVisitor
import com.twitter.rules.core.Emitter
import com.twitter.rules.core.util.findChildrenByClass
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFunction

class ComposeRememberMissing : ComposeKtVisitor {

    override fun visitComposable(function: KtFunction, autoCorrect: Boolean, emitter: Emitter) {
        // To keep memory consumption in check, we first traverse down until we see one of our known functions
        // that need remembering
        function.findChildrenByClass<KtCallExpression>()
            .filter { MethodsThatNeedRemembering.contains(it.calleeExpression?.text) }
            // Only for those, we traverse up to [function], to see if it was actually remembered
            .filterNot { it.isRemembered(function) }
            // If it wasn't, we show the error
            .forEach { callExpression ->
                when (callExpression.calleeExpression!!.text) {
                    "mutableStateOf" -> emitter.report(callExpression, MutableStateOfNotRemembered, false)
                    "derivedStateOf" -> emitter.report(callExpression, DerivedStateOfNotRemembered, false)
                }
            }
    }

    private fun KtCallExpression.isRemembered(stopAt: PsiElement): Boolean {
        var current: PsiElement = parent
        while (current != stopAt) {
            (current as? KtCallExpression)?.let { callExpression ->
                if (callExpression.calleeExpression?.text?.startsWith("remember") == true) return true
            }
            current = current.parent
        }
        return false
    }

    companion object {
        private val MethodsThatNeedRemembering = setOf(
            "derivedStateOf",
            "mutableStateOf",
        )
        val DerivedStateOfNotRemembered = errorMessage("derivedStateOf")
        val MutableStateOfNotRemembered = errorMessage("mutableStateOf")

        private fun errorMessage(name: String): String = """
            Using `$name` in a @Composable function without it being inside of a remember function.
            If you don't remember the state instance, a new state instance will be created when the function is recomposed.

            See https://twitter.github.io/compose-rules/rules/#state-should-be-remembered-in-composables for more information.
        """.trimIndent()
    }
}
