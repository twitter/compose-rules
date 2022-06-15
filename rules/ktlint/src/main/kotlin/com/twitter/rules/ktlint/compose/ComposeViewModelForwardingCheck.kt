package com.twitter.rules.ktlint.compose

import com.twitter.rules.core.definedInInterface
import com.twitter.rules.core.findChildrenByClass
import com.twitter.rules.core.findDirectChildrenByClass
import com.twitter.rules.core.isComposable
import com.twitter.rules.core.isOverride
import com.twitter.rules.core.ktlint.Emitter
import com.twitter.rules.core.ktlint.TwitterKtRule
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.psiUtil.startOffset

class ComposeViewModelForwardingCheck : TwitterKtRule("compose-vm-forwarding-check") {

    override fun visitFile(file: KtFile, autoCorrect: Boolean, emitter: Emitter) {
        file.findChildrenByClass<KtFunction>()
            .filter { it.isComposable && !it.isOverride && !it.definedInInterface }
            .forEach { visitComposable(it, emitter) }
    }

    private fun visitComposable(composable: KtFunction, emitter: Emitter) {
        if (!composable.isComposable) return
        val bodyBlock = composable.bodyBlockExpression ?: return

        // We get here a list of variable names that temptatively contain ViewModels
        val parameters = composable.valueParameterList?.parameters ?: emptyList()
        val viewModelParameterNames = parameters.filter { parameter ->
            // We can't do much better than this. We could look for viewModel() / weaverViewModel() but that
            // would give us way less (and less useful) hits.
            parameter.typeReference?.text?.endsWith("ViewModel") ?: false
        }.mapNotNull { it.name }

        // We want now to see if these parameter names are used in any other calls to functions that start with
        // a capital letter (so, most likely, composables).
        bodyBlock.findDirectChildrenByClass<KtCallExpression>()
            .filter { callExpression -> callExpression.calleeExpression?.text?.first()?.isUpperCase() ?: false }
            .flatMap { callExpression ->
                // Get VALUE_ARGUMENT that has a REFERENCE_EXPRESSION. This would map to `viewModel` in this example:
                // MyComposable(viewModel, ...)
                callExpression.valueArguments
                    .mapNotNull { valueArgument -> valueArgument.getArgumentExpression() as? KtReferenceExpression }
                    .filter { reference -> reference.text in viewModelParameterNames }
                    .map { callExpression }
            }
            .forEach { callExpression ->
                emitter.report(callExpression.startOffset, AvoidViewModelForwarding, false)
            }
    }

    companion object {

        val AvoidViewModelForwarding = """
            Forwarding a ViewModel through multiple @Composable functions should be avoided. Consider using
            state hoisting.

            See https://github.com/twitter/compose-rules/blob/main/docs/rules.md#hoist-all-the-things for more information.
        """.trimIndent()
    }
}
