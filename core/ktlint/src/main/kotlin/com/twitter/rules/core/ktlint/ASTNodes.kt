package com.twitter.rules.core

import com.pinterest.ktlint.core.KtLint
import com.pinterest.ktlint.core.ast.children
import com.pinterest.ktlint.core.ast.isRoot
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.tree.IElementType
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes

/**
 * Finds the root node of the tree.
 * If the node is already the root, return itself
 */
tailrec fun ASTNode.root(): ASTNode = when (isRoot()) {
    true -> this
    false -> treeParent.root()
}

/**
 * Searches the ASTNode's direct descendants for any that match the given filter
 * @Returns a List<ASTNode> of accumulated children that match the filter
 */
fun ASTNode.findDirectChildren(predicate: (ASTNode) -> Boolean): List<ASTNode> {
    return children().filter(predicate).toList()
}

/**
 * Search through all direct children nodes for the given type
 *
 * Recommend using KtStubElementTypes to pass in for the types
 *
 * @param type: type of children ASTNode we want to find
 * @return List<ASTNode> of children nodes that match the type passed in
 */
fun ASTNode.findDirectChildrenByType(type: IElementType): List<ASTNode> =
    findDirectChildren { it.elementType == type }

/**
 * Recursively searches the ASTNode's children to find any that match the given filter
 * @Returns a List<ASTNode> of accumulated children that match the filter
 */
fun ASTNode.findChildren(filter: (ASTNode) -> Boolean): List<ASTNode> {
    tailrec fun findChildrenRecursive(
        astNodes: Sequence<ASTNode>,
        filter: (ASTNode) -> Boolean,
        accumulator: MutableList<ASTNode> = mutableListOf()
    ): List<ASTNode> = if (astNodes.count() == 0) {
        accumulator
    } else {
        accumulator.addAll(astNodes.filter(filter))
        val children = astNodes.asSequence().map { it.children() }.flatten()
        findChildrenRecursive(children, filter, accumulator)
    }

    return findChildrenRecursive(children(), filter)
}

/**
 * Recursively searches ASTNode's children to find any that match the filter
 * If a node matches stopWhen, we don't traverse down that node anymore
 *
 * Example: I have an ASTNode.elementType == CLASS and I want to get all ASTNode of elementType FUNCTION but
 * do not want to include functions of any classes
 *
 * findChildren(
 *     { node -> node.elementType == KtStubElementTypes.FUNCTION },
 *     { node -> node.elementType == KtStubElementTypes.CLASS }
 * )
 */
fun ASTNode.findChildren(filter: (ASTNode) -> Boolean, stopWhen: (ASTNode) -> Boolean): List<ASTNode> {
    tailrec fun findChildrenRecursive(
        astNodes: Sequence<ASTNode>,
        filter: (ASTNode) -> Boolean,
        stopWhen: (ASTNode) -> Boolean,
        accumulator: MutableList<ASTNode> = mutableListOf()
    ): List<ASTNode> {
        return if (astNodes.count() == 0) {
            accumulator
        } else {
            accumulator.addAll(astNodes.filter(filter))
            val validChildren = astNodes.asSequence()
                .map { it.children() }
                .flatten()
                .filter { !stopWhen(it) }
            findChildrenRecursive(validChildren, filter, stopWhen, accumulator)
        }
    }

    return findChildrenRecursive(children(), filter, stopWhen)
}

fun ASTNode.findChildrenByTypes(types: Set<IElementType>, stopAt: IElementType? = null): List<ASTNode> =
    findChildrenByTypes(types, stopAt?.let(::setOf) ?: emptySet())

fun ASTNode.findChildrenByTypes(types: Set<IElementType>, stopAt: Set<IElementType>): List<ASTNode> =
    findChildren({ types.contains(it.elementType) }, { node -> stopAt.any { it == node.elementType } })

/**
 * Recursively search through all children nodes for the given type but stop traversing at a specific type
 * designated by stopAt
 *
 * Recommend using KtStubElementTypes to pass in for the types
 *
 * @param find: types of children ASTNode we want to find
 * @param stopAt: type of ASTNode we want to stop traversing at
 * @return List<ASTNode> of children nodes that match the type passed in
 */
fun ASTNode.findChildrenByType(find: IElementType, stopAt: IElementType? = null): List<ASTNode> =
    findChildrenByType(find, stopAt?.let(::setOf) ?: emptySet())

fun ASTNode.findChildrenByType(find: IElementType, stopAt: Set<IElementType>): List<ASTNode> =
    findChildren({ it.elementType == find }, { node -> stopAt.any { it == node.elementType } })

fun ASTNode.findChildrenByTypeUntil(find: IElementType, stopAt: Set<ASTNode>): List<ASTNode> =
    findChildren({ it.elementType == find }, { node -> stopAt.any { it == node } })

val ASTNode.modifierNames: Sequence<String>
    get() = findChildByType(KtStubElementTypes.MODIFIER_LIST)
        ?.children()
        ?.map { it.text } ?: emptySequence()

fun ASTNode.containsModifier(modifierName: String): Boolean =
    modifierNames.contains(modifierName)

fun ASTNode.containsAllModifiers(vararg modifiers: String): Boolean =
    modifierNames.fold(true) { containsAll, modifier -> modifiers.contains(modifier) && containsAll }

fun ASTNode.containsAnyModifier(vararg modifiers: String): Boolean =
    modifierNames.any { modifiers.contains(it) }

val ASTNode.filePath: String
    get() = getUserData(KtLint.FILE_PATH_USER_DATA_KEY)!!
