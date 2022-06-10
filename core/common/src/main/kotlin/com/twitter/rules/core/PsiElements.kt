package com.twitter.rules.core

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.PsiNameIdentifierOwner
import org.jetbrains.kotlin.psi.psiUtil.startOffset
import java.util.Deque
import java.util.LinkedList

inline fun <reified T : PsiElement> PsiElement.findChildrenByClass(): Sequence<T> =
    sequence {
        val klass = T::class
        val queue: Deque<PsiElement> = LinkedList()
        queue.add(this@findChildrenByClass)
        while (queue.isNotEmpty()) {
            val current = queue.pop()
            if (klass.isInstance(current)) {
                yield(current as T)
            }
            queue.addAll(current.children)
        }
    }

inline fun <reified T : PsiElement> PsiElement.findDirectFirstChildByClass(): T? {
    val klass = T::class
    var current = firstChild
    while (current != null) {
        if (klass.isInstance(current)) {
            return current as T
        }
        current = current.nextSibling
    }
    return null
}

inline fun <reified T : PsiElement> PsiElement.findDirectChildrenByClass(): Sequence<T> =
    sequence {
        val klass = T::class
        var current = firstChild
        while (current != null) {
            if (klass.isInstance(current)) {
                yield(current as T)
            }
            current = current.nextSibling
        }
    }

val PsiNameIdentifierOwner.startOffsetFromName: Int
    get() = nameIdentifier?.startOffset ?: startOffset
