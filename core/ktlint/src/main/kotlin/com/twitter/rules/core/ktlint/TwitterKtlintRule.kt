// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.rules.core.ktlint

import com.pinterest.ktlint.core.Rule
import com.twitter.rules.core.KtElementVisitors
import com.twitter.rules.core.util.isComposable
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes
import org.jetbrains.kotlin.utils.addToStdlib.cast

abstract class TwitterKtlintRule(id: String) : Rule(id), KtElementVisitors {

    final override fun visit(
        node: ASTNode,
        autoCorrect: Boolean,
        emit: (offset: Int, errorMessage: String, canBeAutoCorrected: Boolean) -> Unit
    ) {
        when (node.elementType) {
            KtStubElementTypes.FILE -> visitFile(node.psi.cast(), autoCorrect, emit)
            KtStubElementTypes.CLASS -> visitClass(node.psi.cast(), autoCorrect, emit)
            KtStubElementTypes.FUNCTION -> {
                val function = node.psi.cast<KtFunction>()
                visitFunction(function, autoCorrect, emit)
                if (function.isComposable) {
                    visitComposable(function, autoCorrect, emit)
                }
            }
        }
    }
}
