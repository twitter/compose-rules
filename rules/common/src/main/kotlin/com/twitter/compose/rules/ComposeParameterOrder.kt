// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules

import com.twitter.rules.core.ComposeKtVisitor
import com.twitter.rules.core.Emitter
import com.twitter.rules.core.report
import com.twitter.rules.core.util.isModifier
import com.twitter.rules.core.util.runIf
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtFunctionType
import org.jetbrains.kotlin.psi.KtParameter

class ComposeParameterOrder : ComposeKtVisitor {

    override fun visitComposable(function: KtFunction, autoCorrect: Boolean, emitter: Emitter) {
        // We need to make sure the proper order is respected. It should be:
        // 1. params without defaults
        // 2. modifiers
        // 3. params with defaults
        // 4. optional: function that might have no default

        // Let's try to build the ideal ordering first, and compare against that.
        val currentOrder = function.valueParameters

        // We look in the original params without defaults and see if the last one is a function.
        val hasTrailingFunction = function.hasTrailingFunction
        val trailingLambda = if (hasTrailingFunction) {
            listOf(function.valueParameters.last())
        } else {
            emptyList()
        }

        // We extract the params without with and without defaults, and keep the order between them
        val (withDefaults, withoutDefaults) = function.valueParameters
            .runIf(hasTrailingFunction) { dropLast(1) }
            .partition { it.hasDefaultValue() }

        // As ComposeModifierMissingCheck will catch modifiers without a Modifier default, we don't have to care
        // about that case. We will sort the params with defaults so that the modifier(s) go first.
        val sortedWithDefaults = withDefaults.sortedWith(
            compareByDescending<KtParameter> { it.isModifier }.thenByDescending { it.name == "modifier" },
        )

        // We create our ideal ordering of params for the ideal composable.
        val properOrder = withoutDefaults + sortedWithDefaults + trailingLambda

        // If it's not the same as the current order, we show the rule violation.
        if (currentOrder != properOrder) {
            emitter.report(function, createErrorMessage(currentOrder, properOrder))
        }
    }

    private val KtFunction.hasTrailingFunction: Boolean
        get() = valueParameters.lastOrNull()?.typeReference?.typeElement is KtFunctionType

    companion object {
        fun createErrorMessage(currentOrder: List<KtParameter>, properOrder: List<KtParameter>): String =
            createErrorMessage(currentOrder.joinToString { it.text }, properOrder.joinToString { it.text })

        fun createErrorMessage(currentOrder: String, properOrder: String): String = """
            Parameters in a composable function should be ordered following this pattern: params without defaults, modifiers, params with defaults and optionally, a trailing function that might not have a default param.
            Current params are: [$currentOrder] but should be [$properOrder].

            See https://twitter.github.io/compose-rules/rules/#ordering-composable-parameters-properly for more information.
        """.trimIndent()
    }
}
