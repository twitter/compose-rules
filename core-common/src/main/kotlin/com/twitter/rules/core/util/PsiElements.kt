// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.rules.core.util

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.PsiNameIdentifierOwner
import org.jetbrains.kotlin.psi.psiUtil.startOffset
import java.util.Deque
import java.util.LinkedList

inline fun <reified T : PsiElement> PsiElement.findChildrenByClass(): Sequence<T> =
    sequence {
        val queue: Deque<PsiElement> = LinkedList()
        queue.add(this@findChildrenByClass)
        while (queue.isNotEmpty()) {
            val current = queue.pop()
            if (current is T) {
                yield(current)
            }
            queue.addAll(current.children)
        }
    }

inline fun <reified T : PsiElement> PsiElement.findDirectFirstChildByClass(): T? {
    var current = firstChild
    while (current != null) {
        if (current is T) {
            return current
        }
        current = current.nextSibling
    }
    return null
}

inline fun <reified T : PsiElement> PsiElement.findDirectChildrenByClass(): Sequence<T> =
    sequence {
        var current = firstChild
        while (current != null) {
            if (current is T) {
                yield(current)
            }
            current = current.nextSibling
        }
    }

val PsiNameIdentifierOwner.startOffsetFromName: Int
    get() = nameIdentifier?.startOffset ?: startOffset
