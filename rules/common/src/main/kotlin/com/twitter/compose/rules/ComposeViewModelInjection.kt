// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules

import com.twitter.rules.core.ComposeKtVisitor
import com.twitter.rules.core.Emitter
import com.twitter.rules.core.util.definedInInterface
import com.twitter.rules.core.util.findChildrenByClass
import com.twitter.rules.core.util.findDirectChildrenByClass
import com.twitter.rules.core.util.findDirectFirstChildByClass
import com.twitter.rules.core.util.firstChildLeafOrSelf
import com.twitter.rules.core.util.isOverride
import com.twitter.rules.core.util.lastChildLeafOrSelf
import com.twitter.rules.core.util.nextCodeSibling
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.ElementType
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtFunctionType
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtPsiFactory

class ComposeViewModelInjection : ComposeKtVisitor {

    override fun visitComposable(function: KtFunction, autoCorrect: Boolean, emitter: Emitter) {
        if (function.isOverride || function.definedInInterface) return

        val bodyBlock = function.bodyBlockExpression ?: return

        bodyBlock.findChildrenByClass<KtProperty>()
            .flatMap { property ->
                property.findDirectChildrenByClass<KtCallExpression>()
                    .filter { KnownViewModelFactories.contains(it.calleeExpression?.text) }
                    .map { property to it.calleeExpression!!.text }
            }
            .forEach { (property, viewModelFactoryName) ->
                emitter.report(property, errorMessage(viewModelFactoryName), true)
                if (autoCorrect) {
                    fix(function, property, viewModelFactoryName)
                }
            }
    }

    private fun fix(composable: KtFunction, property: KtProperty, viewModelFactoryName: String) {
        // First of all, we want to extract the property name and all the arguments
        val variableName = property.name
        val callExpression = property.findDirectFirstChildByClass<KtCallExpression>() ?: return
        val argumentList = callExpression.valueArgumentList ?: return

        // We also want the ViewModel type, with two possibilities to support:
        // val viewModel : VM = viewModel(...)
        // val viewModel = viewModel<VM>(...)
        val viewModelTypeReference = property.typeReference
            ?: property.findDirectFirstChildByClass<KtCallExpression>()?.typeArguments?.singleOrNull()
            ?: return

        // Then we need to check the parameters on the FunctionNode. We want to be the last element added
        // EXCEPT in the case in which there is a function as the last parameter, in which case we want to be
        // second to last
        val rawViewModelType = viewModelTypeReference.text
        val rawArgumentList = argumentList.text
        val lastParameters = composable.valueParameters.takeLast(2)
        val parameterList = composable.valueParameterList ?: return

        // Generate the VALUE_PARAMETER for variableName: VMType = viewModel(...)
        val newCode = "$variableName: $rawViewModelType = $viewModelFactoryName$rawArgumentList"
        val factory = KtPsiFactory(parameterList)
        val newParam = factory.createParameter(newCode)

        when {
            // If there are no parameters, we will insert the code directly
            lastParameters.isEmpty() -> parameterList.addParameter(newParam)
            // If the last element is a function, we need to preserve the trailing lambda, so we will insert
            // the code before that last param
            lastParameters.last().typeReference?.typeElement is KtFunctionType -> {
                // If there's only 1 param, we insert the code with the initial parenthesis
                if (lastParameters.size == 1) {
                    val firstToken = parameterList.node.firstChildLeafOrSelf() as LeafPsiElement
                    firstToken.rawReplaceWithText("($newCode, ")
                } else {
                    // If there were 2+ params, we insert the code between the two parameters
                    val lastToken = lastParameters.first()
                        .node
                        .nextCodeSibling()!!
                        .lastChildLeafOrSelf() as LeafPsiElement
                    // Last token here would be the previous comma, if there were spaces between the comma
                    // and the functional type (the next sibling), we would insert ourselves at the left of it.
                    lastToken.rawReplaceWithText("${lastToken.text} $newCode,")
                }
            }

            else -> {
                parameterList.addParameter(newParam)
            }
        }

        // And finally, we can delete the original property from the code
        // 1. If there's whitespace before (code indent spaces) we remove them
        property.node.treePrev?.takeIf { it.elementType == ElementType.WHITE_SPACE }?.psi?.delete()
        // 2. Remove the actual code
        property.delete()
    }

    companion object {

        val KnownViewModelFactories by lazy {
            setOf(
                "viewModel", // AAC VM
                "weaverViewModel", // Weaver
                "hiltViewModel", // Hilt
                "injectedViewModel", // Whetstone
                "mavericksViewModel", // Mavericks
            )
        }

        fun errorMessage(factoryName: String) = """
            Implicit dependencies of composables should be made explicit.

            Usages of $factoryName to acquire a ViewModel should be done in composable default parameters, so that it is more testable and flexible.

            See https://twitter.github.io/compose-rules/rules/#viewmodels for more information.
        """.trimIndent()
    }
}
