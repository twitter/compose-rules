package com.twitter.rules.core.util

import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.CompositeElement
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.ElementType
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl
import org.jetbrains.kotlin.psi.KtImportList
import org.jetbrains.kotlin.psi.psiUtil.children

fun KtImportList.sort() {
    val sortedImports = node.children()
        .filter { it.elementType == KtNodeTypes.IMPORT_DIRECTIVE }
        .sortedBy { it.text }
        .distinctBy { it.text }
    node.removeRange(node.firstChildNode, node.lastChildNode.treeNext)
    sortedImports.forEachIndexed { index, astNode ->
        if (index > 0) {
            node.addChild(PsiWhiteSpaceImpl("\n"), null)
        }
        node.addChild(astNode, null)
    }
}

fun KtImportList.addImports(vararg imports: String) {
    imports.forEach { import ->
        val newImport = CompositeElement(KtNodeTypes.IMPORT_DIRECTIVE).apply {
            rawAddChildren(LeafPsiElement(ElementType.IMPORT_KEYWORD, "import"))
            rawAddChildren(LeafPsiElement(ElementType.WHITE_SPACE, " "))
            import.split('.').forEachIndexed { index, s ->
                if (index != 0) {
                    rawAddChildren(LeafPsiElement(ElementType.DOT, "."))
                }
                rawAddChildren(LeafPsiElement(ElementType.IDENTIFIER, s))
            }
        }
        node.addChild(newImport, null)
    }
    sort()
}
