// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules

import com.twitter.compose.rules.ComposeMultipleContentEmitters.Companion.directUiEmitterCount
import com.twitter.compose.rules.ComposeMultipleContentEmitters.Companion.indirectUiEmitterCount
import com.twitter.rules.core.ComposeKtVisitor
import com.twitter.rules.core.Emitter
import com.twitter.rules.core.report
import com.twitter.rules.core.util.findChildrenByClass
import com.twitter.rules.core.util.hasReceiverType
import com.twitter.rules.core.util.isComposable
import com.twitter.rules.core.util.returnsValue
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunction

class ComposeContentEmitterReturningValues : ComposeKtVisitor {

    override fun visitFile(file: KtFile, autoCorrect: Boolean, emitter: Emitter) {
        val composableToEmissionCount = file.findChildrenByClass<KtFunction>()
            .filter { it.isComposable }
            // We don't want to analyze composables that are extension functions, as they might be things like
            // BoxScope which are legit, and we want to avoid false positives.
            .filter { it.hasBlockBody() }
            // We want only methods with a body
            .filterNot { it.hasReceiverType }
            // Now we want to get the count of direct emitters in them: the composables we know for a fact that output UI
            .associateWith { it.directUiEmitterCount }

        // Now we can give some extra passes through the list of composables, and try to get a more accurate count.
        // We want to make sure that if these composables are using other composables in this file that emit UI,
        // those are taken into account too. For example:
        // @Composable fun Comp1() { Text("Hi") }
        // @Composable fun Comp2() { Text("Hola") }
        // @Composable fun Comp3() { Comp1() Comp2() } // This wouldn't be picked up at first, but should after 1 loop
        var current = composableToEmissionCount

        var shouldMakeAnotherPass = true
        while (shouldMakeAnotherPass) {
            val updatedMapping = current.mapValues { (functionNode, _) ->
                functionNode.indirectUiEmitterCount(current)
            }
            when {
                updatedMapping != current -> current = updatedMapping
                else -> shouldMakeAnotherPass = false
            }
        }

        // Data in currentMapping should have all the # of emissions inferred for each composable in this file,
        // so we want to iterate through all of them
        current.filterValues { it > 0 }.keys
            // If the function doesn't have a return type or returns Unit explicitly, it's valid. Otherwise, show error.
            .filter { it.returnsValue }
            // In here we will have functions that emit UI and return a type other than Unit, which is no bueno.
            .forEach { composable ->
                emitter.report(composable, ContentEmitterReturningValuesToo)
            }
    }

    companion object {

        val ContentEmitterReturningValuesToo = """
            Composable functions should either emit content into the composition or return a value, but not both.
            If a composable should offer additional control surfaces to its caller, those control surfaces or callbacks
            should be provided as parameters to the composable function by the caller.

            See https://twitter.github.io/compose-rules/rules/#do-not-emit-content-and-return-a-result for more information.
        """.trimIndent()
    }
}
