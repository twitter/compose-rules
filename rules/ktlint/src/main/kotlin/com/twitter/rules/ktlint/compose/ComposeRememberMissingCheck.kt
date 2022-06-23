package com.twitter.rules.ktlint.compose

import com.twitter.rules.core.findChildrenByClass
import com.twitter.rules.core.isComposable
import com.twitter.rules.core.ktlint.Emitter
import com.twitter.rules.core.ktlint.TwitterKtRule
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.psiUtil.startOffset

class ComposeRememberMissingCheck : TwitterKtRule("twitter-compose:remember-missing-check") {

    override fun visitFunction(function: KtFunction, autoCorrect: Boolean, emitter: Emitter) {
        if (!function.isComposable) return

        // To keep memory consumption in check, we first traverse down until we see one of our known functions
        // that need remembering
        function.findChildrenByClass<KtCallExpression>()
            .filter { MethodsThatNeedRemembering.contains(it.calleeExpression?.text) }
            // Only for those, we traverse up to [function], to see if it was actually remembered
            .filterNot { it.isRemembered(function) }
            // If it wasn't, we show the error
            .forEach { callExpression ->
                when (callExpression.calleeExpression!!.text) {
                    "mutableStateOf" -> emitter.report(callExpression.startOffset, MutableStateOfNotRemembered, false)
                    "derivedStateOf" -> emitter.report(callExpression.startOffset, DerivedStateOfNotRemembered, false)
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
            "mutableStateOf"
        )
        val DerivedStateOfNotRemembered = errorMessage("derivedStateOf")
        val MutableStateOfNotRemembered = errorMessage("mutableStateOf")

        private fun errorMessage(name: String): String = """
            Using `$name` in a @Composable function without it being inside of a remember function.
            If you don't remember the state instance, a new state instance will be created when the function is recomposed.

            For more information: https://github.com/twitter/compose-rules/blob/main/docs/rules.md#state-should-be-remembered-in-composables
        """.trimIndent()
    }
}
