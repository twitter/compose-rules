// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.rules.core.ktlint

import com.pinterest.ktlint.core.Rule
import com.pinterest.ktlint.core.api.EditorConfigProperties
import com.twitter.rules.core.ComposeKtConfig
import com.twitter.rules.core.ComposeKtConfig.Companion.attach
import com.twitter.rules.core.ComposeKtVisitor
import com.twitter.rules.core.Emitter
import com.twitter.rules.core.util.isComposable
import com.twitter.rules.core.util.startOffsetFromName
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.PsiNameIdentifierOwner
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.psiUtil.startOffset
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes
import org.jetbrains.kotlin.utils.addToStdlib.cast

abstract class TwitterKtlintRule(id: String) : Rule(id), ComposeKtVisitor {

    private lateinit var properties: EditorConfigProperties

    override fun beforeFirstNode(editorConfigProperties: EditorConfigProperties) {
        properties = editorConfigProperties
    }

    private val config: ComposeKtConfig by lazy { KtlintComposeKtConfig(properties) }

    @Suppress("DEPRECATION")
    final override fun beforeVisitChildNodes(
        node: ASTNode,
        autoCorrect: Boolean,
        emit: (offset: Int, errorMessage: String, canBeAutoCorrected: Boolean) -> Unit,
    ) {
        val psi = node.psi
        when (node.elementType) {
            KtStubElementTypes.FILE -> {
                psi.attach(config)
                visitFile(psi.cast(), autoCorrect, emit.toEmitter())
            }

            KtStubElementTypes.CLASS -> visitClass(psi.cast(), autoCorrect, emit.toEmitter())
            KtStubElementTypes.FUNCTION -> {
                val function = psi.cast<KtFunction>()
                val emitter = emit.toEmitter()
                visitFunction(function, autoCorrect, emitter)
                if (function.isComposable) {
                    visitComposable(function, autoCorrect, emitter)
                }
            }
        }
    }

    private fun ((Int, String, Boolean) -> Unit).toEmitter() = Emitter { element, errorMessage, canBeAutoCorrected ->
        val offset = if (element is PsiNameIdentifierOwner) {
            element.startOffsetFromName
        } else {
            element.startOffset
        }
        invoke(offset, errorMessage, canBeAutoCorrected)
    }
}
