// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules

import com.twitter.rules.core.ComposeKtVisitor
import com.twitter.rules.core.Emitter
import com.twitter.rules.core.util.emitsContent
import com.twitter.rules.core.util.findChildrenByClass
import com.twitter.rules.core.util.modifierParameter
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.KtValueArgumentName
import org.jetbrains.kotlin.psi.psiUtil.siblings

class ComposeModifierReused : ComposeKtVisitor {

    override fun visitComposable(function: KtFunction, autoCorrect: Boolean, emitter: Emitter) {
        if (!function.emitsContent) return
        val composableBlockExpression = function.bodyBlockExpression ?: return
        val modifier = function.modifierParameter ?: return
        val initialName = modifier.name ?: return

        // Try to get all possible names by iterating on possible name reassignments until it's stable
        val modifierNames = composableBlockExpression.obtainAllModifierNames(initialName)

        // Find all composable-looking CALL_EXPRESSIONs that are using any of these modifier names
        composableBlockExpression.findChildrenByClass<KtCallExpression>()
            .filter { it.calleeExpression?.text?.first()?.isUpperCase() == true }
            .filter { it.isUsingModifiers(modifierNames) }
            .map { callExpression ->
                // To get an accurate count (and respecting if/when/whatever different branches)
                // we'll need to traverse upwards to [function] from each one of these usages
                // to see the real amount of usages.
                buildSet<KtCallExpression> {
                    var current: PsiElement = callExpression
                    while (current != composableBlockExpression) {
                        // If the current element is a CALL_EXPRESSION and using modifiers, log it
                        if (current is KtCallExpression && current.isUsingModifiers(modifierNames)) {
                            add(current)
                        }
                        // If any of the siblings also use any of these, we also log them.
                        // This is for the special case where only sibling composables reuse modifiers
                        addAll(
                            current.siblings()
                                .filterIsInstance<KtCallExpression>()
                                .filter { it.isUsingModifiers(modifierNames) },
                        )
                        current = current.parent
                    }
                }
            }
            // Any set with more than 1 item is interesting to us: means there is a rule violation
            .filter { it.size > 1 }
            // At this point we have all the grouping of violations, so we just need to extract all individual
            // items from them as we are no longer interested in the groupings, but their individual elements
            .flatten()
            // We don't want to double report
            .distinct()
            .forEach { callExpression ->
                emitter.report(callExpression, ModifierShouldBeUsedOnceOnly, false)
            }
    }

    private fun KtCallExpression.isUsingModifiers(modifierNames: List<String>): Boolean =
        valueArguments.any { argument ->
            when (val expression = argument.getArgumentExpression()) {
                // if it's MyComposable(modifier) or similar
                is KtReferenceExpression -> {
                    modifierNames.contains(expression.text)
                }
                // if it's MyComposable(modifier.fillMaxWidth()) or similar
                is KtDotQualifiedExpression -> {
                    // On cases of multiple nested KtDotQualifiedExpressions (e.g. multiple chained methods)
                    // we need to iterate until we find the start of the chain
                    modifierNames.contains(expression.rootExpression.text)
                }

                else -> false
            }
        }

    private val KtDotQualifiedExpression.rootExpression: KtExpression
        get() {
            var current: KtExpression = receiverExpression
            while (current is KtDotQualifiedExpression) {
                current = current.receiverExpression
            }
            return current
        }

    private fun KtBlockExpression.obtainAllModifierNames(initialName: String): List<String> {
        var lastSize = 0
        val tempModifierNames = mutableSetOf(initialName)
        while (lastSize < tempModifierNames.size) {
            lastSize = tempModifierNames.size
            // Find usages in the current block (the original composable)
            tempModifierNames += findModifierManipulations { tempModifierNames.contains(it) }
            // Find usages in child composable blocks
            tempModifierNames += findChildrenByClass<KtBlockExpression>()
                .flatMap { block -> block.findModifierManipulations { tempModifierNames.contains(it) } }
        }
        return tempModifierNames.toList()
    }

    /**
     * Find references to modifier as a property in case they try to modify or reuse the modifier that way
     * E.g. val modifier2 = if (X) modifier.blah() else modifier.bleh()
     */
    private fun KtBlockExpression.findModifierManipulations(contains: (String) -> Boolean): List<String> =
        statements
            .filterIsInstance<KtProperty>()
            .flatMap { property ->
                property.findChildrenByClass<KtReferenceExpression>()
                    .filter { referenceExpression ->
                        val parent = referenceExpression.parent
                        parent !is KtCallExpression &&
                            parent !is KtValueArgumentName &&
                            contains(referenceExpression.text)
                    }
                    .map { property }
            }
            .mapNotNull { it.nameIdentifier?.text }

    companion object {
        val ModifierShouldBeUsedOnceOnly = """
            Modifiers should only be used once and by the root level layout of a Composable. This is true even if appended to or with other modifiers e.g. 'modifier.fillMaxWidth()'.

            Use Modifier (with a capital 'M') to construct a new Modifier that you can pass to other composables.

            See https://twitter.github.io/compose-rules/rules/#dont-re-use-modifiers for more information.
        """.trimIndent()
    }
}
