// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules.ktlint

import com.pinterest.ktlint.test.KtLintAssertThat.Companion.assertThatRule
import com.pinterest.ktlint.test.LintViolation
import com.twitter.compose.rules.ComposeModifierComposable
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
                detail = ComposeModifierComposable.ComposableModifier,
            ),
            LintViolation(
                line = 4,
                col = 14,
                detail = ComposeModifierComposable.ComposableModifier,
            ),
        )
    }

    @Test
    fun `do not error on a regular composable`() {
        @Language("kotlin")
        val code = """
            @Composable
            fun TextHolder(text: String) {}
        """.trimIndent()

        modifierRuleAssertThat(code).hasNoLintViolations()
    }
}
