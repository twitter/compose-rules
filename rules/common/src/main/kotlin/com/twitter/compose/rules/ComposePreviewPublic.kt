// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules

import com.twitter.rules.core.ComposeKtVisitor
import com.twitter.rules.core.Emitter
import com.twitter.rules.core.util.isPreview
import com.twitter.rules.core.util.isPreviewParameter
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.psiUtil.isPublic

class ComposePreviewPublic : ComposeKtVisitor {

    override fun visitComposable(function: KtFunction, autoCorrect: Boolean, emitter: Emitter) {
        // We only want previews
        if (!function.isPreview) return
        // We only care about public methods
        if (!function.isPublic) return
        // If the method is public, none of it's params should be tagged as preview
        if (function.valueParameters.none { it.isPreviewParameter }) return

        // If we got here, it's a public method in a @Preview composable with a @PreviewParameter parameter
        emitter.report(function, ComposablesPreviewShouldNotBePublic, true)
        if (autoCorrect) {
            function.addModifier(KtTokens.PRIVATE_KEYWORD)
        }
    }

    companion object {
        val ComposablesPreviewShouldNotBePublic = """
            Composables annotated with @Preview that are used only for previewing the UI should not be public.

            See https://twitter.github.io/compose-rules/rules/#preview-composables-should-not-be-public for more information.
        """.trimIndent()
    }
}
