package com.twitter.rules.core

import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierType

val KtFunction.returnsValue: Boolean
    get() = typeReference != null && typeReference!!.text != "Unit"

val KtFunction.isPrivate: Boolean
    get() = visibilityModifierType() == KtTokens.PRIVATE_KEYWORD

val KtFunction.isProtected: Boolean
    get() = visibilityModifierType() == KtTokens.PROTECTED_KEYWORD

val KtFunction.isInternal: Boolean
    get() = visibilityModifierType() == KtTokens.INTERNAL_KEYWORD

val KtFunction.isOverride: Boolean
    get() = hasModifier(KtTokens.OVERRIDE_KEYWORD)
