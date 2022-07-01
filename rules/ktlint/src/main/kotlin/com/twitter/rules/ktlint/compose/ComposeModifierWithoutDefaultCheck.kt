package com.twitter.rules.ktlint.compose

import com.pinterest.ktlint.core.ast.lastChildLeafOrSelf
import com.twitter.rules.core.Emitter
import com.twitter.rules.core.ktlint.TwitterKtlintRule
import com.twitter.rules.core.report
import com.twitter.rules.core.util.definedInInterface
import com.twitter.rules.core.util.isModifier
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.psi.KtFunction

class ComposeModifierWithoutDefaultCheck : TwitterKtlintRule("twitter-compose:modifier-without-default-check") {

    override fun visitComposable(function: KtFunction, autoCorrect: Boolean, emitter: Emitter) {
        if (function.definedInInterface) return

        // Look for modifier params in the composable signature, and if any without a default value is found, error out.
        function.valueParameters.filter { it.isModifier }
            .filterNot { it.hasDefaultValue() }
            .forEach { modifierParameter ->
                emitter.report(modifierParameter, MissingModifierDefaultParam, true)

                // This error is easily auto fixable, we just inject ` = Modifier` to the param
                if (autoCorrect) {
                    val lastToken = modifierParameter.node.lastChildLeafOrSelf() as LeafPsiElement
                    val currentText = lastToken.text
                    lastToken.rawReplaceWithText("$currentText = Modifier")
                }
            }
    }

    companion object {
        val MissingModifierDefaultParam = """
            This @Composable function has a modifier parameter but it doesn't have a default value.

            See https://github.com/twitter/compose-rules/blob/main/docs/rules.md#modifiers-should-have-default-parameters for more information.
        """.trimIndent()
    }
}
