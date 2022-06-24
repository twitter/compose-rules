package com.twitter.rules.core.util

import org.jetbrains.kotlin.psi.KtAnnotated

val KtAnnotated.isComposable: Boolean
    get() = annotationEntries.any { it.calleeExpression?.text == "Composable" }
