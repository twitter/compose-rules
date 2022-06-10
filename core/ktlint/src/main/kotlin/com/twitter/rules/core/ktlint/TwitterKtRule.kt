package com.twitter.rules.core.ktlint

import com.pinterest.ktlint.core.Rule
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes
import org.jetbrains.kotlin.utils.addToStdlib.cast

abstract class TwitterKtRule(id: String) : Rule(id) {

    final override fun visit(
        node: ASTNode,
        autoCorrect: Boolean,
        emit: (offset: Int, errorMessage: String, canBeAutoCorrected: Boolean) -> Unit
    ) {
        when (node.elementType) {
            KtStubElementTypes.FILE -> visitFile(node.psi.cast(), autoCorrect, emit)
            KtStubElementTypes.CLASS -> visitClass(node.psi.cast(), autoCorrect, emit)
            KtStubElementTypes.FUNCTION -> visitFunction(node.psi.cast(), autoCorrect, emit)
        }
    }

    protected open fun visitFunction(function: KtFunction, autoCorrect: Boolean, emitter: Emitter) {}

    protected open fun visitClass(clazz: KtClass, autoCorrect: Boolean, emitter: Emitter) {}

    protected open fun visitFile(file: KtFile, autoCorrect: Boolean, emitter: Emitter) {}
}
