// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules

import com.twitter.rules.core.ComposeKtConfig.Companion.config
import com.twitter.rules.core.ComposeKtVisitor
import com.twitter.rules.core.Emitter
import com.twitter.rules.core.report
import com.twitter.rules.core.util.emitsContent
import com.twitter.rules.core.util.findChildrenByClass
import com.twitter.rules.core.util.hasReceiverType
import com.twitter.rules.core.util.isComposable
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunction

class ComposeMultipleContentEmitters : ComposeKtVisitor {

    override fun visitFile(file: KtFile, autoCorrect: Boolean, emitter: Emitter) {
        val excluded = file.config().getSet("exclude", emptySet())
        // CHECK #1 : We want to find the composables first that are at risk of emitting content from multiple sources.
        val composables = file.findChildrenByClass<KtFunction>()
            .filter { it.isComposable }
            // We don't want to analyze composables that are extension functions, as they might be things like
            // BoxScope which are legit, and we want to avoid false positives.
            .filter { it.hasBlockBody() }
            // We want only methods with a body
            .filterNot { it.hasReceiverType }
            .filterNot { excluded.contains(it.name) }

        // Now we want to get the count of direct emitters in them: the composables we know for a fact that output UI
        val composableToEmissionCount = composables.associateWith { it.directUiEmitterCount }

        // We can start showing errors, for composables that emit more than once (from the list of known composables)
        val directEmissionsReported = composableToEmissionCount.filterValues { it > 1 }.keys
        for (composable in directEmissionsReported) {
            emitter.report(composable, MultipleContentEmittersDetected)
        }

        // Now we can give some extra passes through the list of composables, and try to get a more accurate count.
        // We want to make sure that if these composables are using other composables in this file that emit UI,
        // those are taken into account too. For example:
        // @Composable fun Comp1() { Text("Hi") }
        // @Composable fun Comp2() { Text("Hola") }
        // @Composable fun Comp3() { Comp1() Comp2() } // This wouldn't be picked up at first, but should after 1 loop
        var currentMapping = composableToEmissionCount

        var shouldMakeAnotherPass = true
        while (shouldMakeAnotherPass) {
            val updatedMapping = currentMapping.mapValues { (functionNode, _) ->
                functionNode.indirectUiEmitterCount(currentMapping)
            }
            when {
                updatedMapping != currentMapping -> currentMapping = updatedMapping
                else -> shouldMakeAnotherPass = false
            }
        }

        // Here we have the settled data after all the needed passes, so we want to show errors for them,
        // if they were not caught already by the 1st emission loop
        currentMapping.filterValues { it > 1 }
            .filterNot { directEmissionsReported.contains(it.key) }
            .keys
            .forEach { composable ->
                emitter.report(composable, MultipleContentEmittersDetected)
            }
    }

    companion object {
        internal val KtFunction.directUiEmitterCount: Int
            get() = bodyBlockExpression?.let { block ->
                block.statements
                    .filterIsInstance<KtCallExpression>()
                    .count { it.emitsContent }
            } ?: 0

        internal fun KtFunction.indirectUiEmitterCount(mapping: Map<KtFunction, Int>): Int {
            val bodyBlock = bodyBlockExpression ?: return 0
            return bodyBlock.statements
                .filterIsInstance<KtCallExpression>()
                .count { callExpression ->
                    // If it's a direct hit on our list, it should count directly
                    if (callExpression.emitsContent) return@count true

                    val name = callExpression.calleeExpression?.text ?: return@count false
                    // If the hit is in the provided mapping, it means it is using a composable that we know emits UI,
                    // that we inferred from previous passes
                    val value = mapping.mapKeys { entry -> entry.key.name }.getOrElse(name) { return@count false }
                    value > 0
                }
        }

        val MultipleContentEmittersDetected = """
            Composable functions should only be emitting content into the composition from one source at their top level.

            See https://twitter.github.io/compose-rules/rules/#do-not-emit-multiple-pieces-of-content for more information.
        """.trimIndent()
    }
}
