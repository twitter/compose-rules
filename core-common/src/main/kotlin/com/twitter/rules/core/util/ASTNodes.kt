// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.rules.core.util

import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.PsiComment
import org.jetbrains.kotlin.lexer.KtTokens

fun ASTNode.lastChildLeafOrSelf(): ASTNode {
    var node = this
    if (node.lastChildNode != null) {
        do {
            node = node.lastChildNode
        } while (node.lastChildNode != null)
        return node
    }
    return node
}

fun ASTNode.firstChildLeafOrSelf(): ASTNode {
    var node = this
    if (node.firstChildNode != null) {
        do {
            node = node.firstChildNode
        } while (node.firstChildNode != null)
        return node
    }
    return node
}

fun ASTNode.parent(p: (ASTNode) -> Boolean, strict: Boolean = true): ASTNode? {
    var n: ASTNode? = if (strict) this.treeParent else this
    while (n != null) {
        if (p(n)) {
            return n
        }
        n = n.treeParent
    }
    return null
}

fun ASTNode.isPartOfComment(): Boolean =
    parent({ it.psi is PsiComment }, strict = false) != null

fun ASTNode.nextCodeSibling(): ASTNode? =
    nextSibling { it.elementType != KtTokens.WHITE_SPACE && !it.isPartOfComment() }

inline fun ASTNode.nextSibling(p: (ASTNode) -> Boolean): ASTNode? {
    var node = treeNext
    while (node != null) {
        if (p(node)) {
            return node
        }
        node = node.treeNext
    }
    return null
}
