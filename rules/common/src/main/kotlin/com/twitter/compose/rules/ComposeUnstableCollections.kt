// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules

import com.twitter.rules.core.ComposeKtVisitor
import com.twitter.rules.core.Emitter
import com.twitter.rules.core.report
import com.twitter.rules.core.util.isTypeUnstableCollection
import org.jetbrains.kotlin.psi.KtFunction
import java.util.*

class ComposeUnstableCollections : ComposeKtVisitor {

    override fun visitComposable(function: KtFunction, autoCorrect: Boolean, emitter: Emitter) {
        for (param in function.valueParameters.filter { it.isTypeUnstableCollection }) {
            val variableName = param.nameAsSafeName.asString()
            val type = param.typeReference?.text ?: "List/Set/Map"
            val message = createErrorMessage(
                type = type,
                rawType = type.replace(DiamondRegex, ""),
                variable = variableName,
            )
            emitter.report(param.typeReference ?: param, message)
        }
    }

    companion object {
        private val DiamondRegex by lazy(LazyThreadSafetyMode.NONE) { Regex("<.*>\\??") }
        private val String.capitalized: String
            get() = replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

        fun createErrorMessage(type: String, rawType: String, variable: String) = """
            The Compose Compiler cannot infer the stability of a parameter if a $type is used in it, even if the item type is stable.
            You should use Kotlinx Immutable Collections instead: `$variable: Immutable$type` or create an `@Immutable` wrapper for this class: `@Immutable data class ${variable.capitalized}$rawType(val items: $type)`

            See https://twitter.github.io/compose-rules/rules/#avoid-using-unstable-collections for more information.
        """.trimIndent()
    }
}
