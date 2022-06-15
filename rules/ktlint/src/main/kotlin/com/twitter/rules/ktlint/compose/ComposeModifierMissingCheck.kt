package com.twitter.rules.ktlint.compose

import com.pinterest.ktlint.core.ast.lastChildLeafOrSelf
import com.twitter.rules.core.definedInInterface
import com.twitter.rules.core.emitsContent
import com.twitter.rules.core.findChildrenByClass
import com.twitter.rules.core.isComposable
import com.twitter.rules.core.isOverride
import com.twitter.rules.core.ktlint.Emitter
import com.twitter.rules.core.ktlint.TwitterKtRule
import com.twitter.rules.core.ktlint.report
import com.twitter.rules.core.modifierParameter
import com.twitter.rules.core.returnsValue
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.psiUtil.isPublic

class ComposeModifierMissingCheck : TwitterKtRule("compose-modifier-check") {

    override fun visitFile(file: KtFile, autoCorrect: Boolean, emitter: Emitter) {
        file.findChildrenByClass<KtFunction>()
            .filter { it.isComposable }
            .filter {
                // We want to find all composable functions that:
                //  - are public
                //  - emit content
                //  - are not overriden or part of an interface
                it.isPublic && !it.returnsValue && !it.isOverride && !it.definedInInterface
            }
            .forEach { visitComposable(it, autoCorrect, emitter) }
    }

    private fun visitComposable(composable: KtFunction, autoCorrect: Boolean, emitter: Emitter) {
        // First we look for a modifier param.
        composable.modifierParameter?.let { modifierParameter ->
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
        if (composable.emitsContent) {
            emitter.report(composable, MissingModifierContentComposable)
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
