// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules

import com.twitter.rules.core.ComposeKtVisitor
import com.twitter.rules.core.Emitter
import com.twitter.rules.core.report
import com.twitter.rules.core.util.isTypeMutable
import org.jetbrains.kotlin.psi.KtFunction

class ComposeMutableParameters : ComposeKtVisitor {

    override fun visitComposable(function: KtFunction, autoCorrect: Boolean, emitter: Emitter) {
        function.valueParameters
            .filter { it.isTypeMutable }
            .forEach { emitter.report(it, MutableParameterInCompose) }
    }

    companion object {

        val MutableParameterInCompose = """
            Using mutable objects as state in Compose will cause your users to see incorrect or stale data in your app.
            Mutable objects that are not observable, such as ArrayList<T> or a mutable data class, cannot be observed by
            Compose to trigger recomposition when they change.

            See https://twitter.github.io/compose-rules/rules/#do-not-use-inherently-mutable-types-as-parameters for more information.
        """.trimIndent()
    }
}
