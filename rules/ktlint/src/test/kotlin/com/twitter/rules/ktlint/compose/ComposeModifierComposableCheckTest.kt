// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.rules.ktlint.compose

import com.pinterest.ktlint.test.KtLintAssertThat.Companion.assertThatRule
import com.pinterest.ktlint.test.LintViolation
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

class ComposeModifierComposableCheckTest {

    private val modifierRuleAssertThat = assertThatRule { ComposeModifierComposableCheck() }

    @Test
    fun `errors when a composable Modifier extension is detected`() {
        @Language("kotlin")
        val code =
            """
                @Composable
                fun Modifier.something(): Modifier { }
                @Composable
                fun Modifier.something() = somethingElse()
            """.trimIndent()

        modifierRuleAssertThat(code).hasLintViolationsWithoutAutoCorrect(
            LintViolation(
                line = 2,
                col = 14,
                detail = ComposeModifierComposableCheck.ComposableModifier
            ),
            LintViolation(
                line = 4,
                col = 14,
                detail = ComposeModifierComposableCheck.ComposableModifier
            )
        )
    }
}
