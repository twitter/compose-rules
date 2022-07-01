package com.twitter.rules.ktlint.compose

import com.pinterest.ktlint.core.ast.lastChildLeafOrSelf
import com.twitter.rules.core.Emitter
import com.twitter.rules.core.ktlint.TwitterKtlintRule
import com.twitter.rules.core.report
import com.twitter.rules.core.util.definedInInterface
import com.twitter.rules.core.util.emitsContent
import com.twitter.rules.core.util.isOverride
import com.twitter.rules.core.util.modifierParameter
import com.twitter.rules.core.util.returnsValue
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.psiUtil.isPublic

class ComposeModifierMissingCheck : TwitterKtlintRule("twitter-compose:modifier-check") {

    override fun visitComposable(function: KtFunction, autoCorrect: Boolean, emitter: Emitter) {
        // We want to find all composable functions that:
        //  - are public
        //  - emit content
        //  - are not overriden or part of an interface
        if (!function.isPublic || function.returnsValue || function.isOverride || function.definedInInterface) {
            return
        }

        // First we look for a modifier param.
        function.modifierParameter?.let { modifierParameter ->
            // If found, we have to check if it has a default value, which should be `Modifier`

            if (!modifierParameter.hasDefaultValue()) {
                emitter.report(modifierParameter, MissingModifierDefaultParam, true)
            }

            // This error is easily auto fixable, we just inject ` = Modifier` to the param
            if (autoCorrect) {
                val lastToken = modifierParameter.node.lastChildLeafOrSelf() as LeafPsiElement
                val currentText = lastToken.text
                lastToken.rawReplaceWithText("$currentText = Modifier")
            }

            // As we found a modifier, we can bail here.
            return
        }

        // In case we didn't find any `modifier` parameters, we check if it emits content and report the error if so.
        if (function.emitsContent) {
            emitter.report(function, MissingModifierContentComposable)
        }
    }

    companion object {
        val MissingModifierDefaultParam = """
            This @Composable function has a modifier parameter but it doesn't have a default value.

            See https://github.com/twitter/compose-rules/blob/main/docs/rules.md#when-should-i-expose-modifier-parameters for more information.
        """.trimIndent()

        val MissingModifierContentComposable = """
            This @Composable function emits content but doesn't have a modifier parameter.

            See https://github.com/twitter/compose-rules/blob/main/docs/rules.md#when-should-i-expose-modifier-parameters for more information.
        """.trimIndent()
    }
}
