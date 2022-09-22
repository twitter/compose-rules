// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.rules.core.util

import com.twitter.rules.core.ComposeKtConfig.Companion.config
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtParameter

val KtFunction.emitsContent: Boolean
    get() = if (isComposable) findChildrenByClass<KtCallExpression>().any { it.emitsContent } else false

val KtCallExpression.emitsContent: Boolean
    get() {
        val methodName = calleeExpression?.text ?: return false
        val providedContentEmitters = config().getSet("contentEmitters", emptySet())
        return ComposableEmittersList.contains(methodName) ||
            ComposableEmittersListRegex.matches(methodName) ||
            providedContentEmitters.contains(methodName) ||
            containsComposablesWithModifiers
    }

private val KtCallExpression.containsComposablesWithModifiers: Boolean
    get() = valueArguments
        .filter { it.isNamed() }
        .any { it.getArgumentName()?.text == "modifier" }

/**
 * This is an allowlist with common composables that emit content. Feel free to add more elements if you stumble
 * upon them in code reviews that should have triggered an error from this rule.
 */
private val ComposableEmittersList by lazy {
    setOf(
        // androidx.compose.foundation
        "BasicTextField",
        "Box",
        "Canvas",
        "ClickableText",
        "Column",
        "Icon",
        "Image",
        "Layout",
        "LazyColumn",
        "LazyRow",
        "LazyVerticalGrid",
        "Row",
        "Text",
        // android.compose.material
        "BottomDrawer",
        "Button",
        "Card",
        "Checkbox",
        "CircularProgressIndicator",
        "Divider",
        "DropdownMenu",
        "DropdownMenuItem",
        "ExposedDropdownMenuBox",
        "ExtendedFloatingActionButton",
        "FloatingActionButton",
        "IconButton",
        "IconToggleButton",
        "LeadingIconTab",
        "LinearProgressIndicator",
        "ListItem",
        "ModalBottomSheetLayout",
        "ModalDrawer",
        "NavigationRail",
        "NavigationRailItem",
        "OutlinedButton",
        "OutlinedTextField",
        "RadioButton",
        "Scaffold",
        "ScrollableTabRow",
        "Slider",
        "SnackbarHost",
        "Surface",
        "SwipeToDismiss",
        "Switch",
        "Tab",
        "TabRow",
        "TextButton",
        "TopAppBar",
        // Accompanist
        "BottomNavigation",
        "BottomNavigationContent",
        "BottomNavigationSurface",
        "FlowColumn",
        "FlowRow",
        "HorizontalPager",
        "HorizontalPagerIndicator",
        "SwipeRefresh",
        "SwipeRefreshIndicator",
        "TopAppBarContent",
        "TopAppBarSurface",
        "VerticalPager",
        "VerticalPagerIndicator",
        "WebView"
    )
}

val ComposableEmittersListRegex by lazy {
    Regex(
        listOf(
            "Spacer\\d*" // Spacer() + SpacerNUM()
        ).joinToString(
            separator = "|",
            prefix = "(",
            postfix = ")"
        )
    )
}

val ModifierNames = setOf(
    "Modifier",
    "GlanceModifier"
)

val KtCallableDeclaration.isModifier: Boolean
    get() = ModifierNames.contains(typeReference?.text)

val KtCallableDeclaration.isModifierReceiver: Boolean
    get() = ModifierNames.contains(receiverTypeReference?.text)

val KtFunction.modifierParameter: KtParameter?
    get() {
        val modifiers = valueParameters.filter { it.isModifier }
        return modifiers.firstOrNull { it.name == "modifier" } ?: modifiers.firstOrNull()
    }
