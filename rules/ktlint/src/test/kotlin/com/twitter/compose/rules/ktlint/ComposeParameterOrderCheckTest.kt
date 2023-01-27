// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules.ktlint

import com.pinterest.ktlint.test.KtLintAssertThat.Companion.assertThatRule
import com.pinterest.ktlint.test.LintViolation
import com.twitter.compose.rules.ComposeParameterOrder
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

class ComposeParameterOrderCheckTest {

    private val orderingRuleAssertThat = assertThatRule { ComposeParameterOrderCheck() }

    @Test
    fun `no errors when ordering is correct`() {
        @Language("kotlin")
        val code = """
            fun MyComposable(text1: String, modifier: Modifier = Modifier, other: String = "1", other2: String = "2") { }

            @Composable
            fun MyComposable(text1: String, modifier: Modifier = Modifier, other2: String = "2", other : String = "1") { }

            @Composable
            fun MyComposable(text1: String, modifier: Modifier = Modifier, trailing: () -> Unit) { }

            @Composable
            fun MyComposable(text1: String, modifier: Modifier = Modifier, m2: Modifier = Modifier, trailing: () -> Unit) { }
        """.trimIndent()
        orderingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `errors found when ordering is wrong`() {
        @Language("kotlin")
        val code = """
            @Composable
            fun MyComposable(modifier: Modifier = Modifier, other: String, other2: String) { }

            @Composable
            fun MyComposable(text: String = "deffo", modifier: Modifier = Modifier) { }

            @Composable
            fun MyComposable(modifier: Modifier = Modifier, text: String = "123", modifier2: Modifier = Modifier) { }

            @Composable
            fun MyComposable(text: String = "123", modifier: Modifier = Modifier, lambda: () -> Unit) { }

            @Composable
            fun MyComposable(text1: String, m2: Modifier = Modifier, modifier: Modifier = Modifier, trailing: () -> Unit) { }
        """.trimIndent()
        orderingRuleAssertThat(code).hasLintViolationsWithoutAutoCorrect(
            LintViolation(
                line = 2,
                col = 5,
                detail = ComposeParameterOrder.createErrorMessage(
                    currentOrder = "modifier: Modifier = Modifier, other: String, other2: String",
                    properOrder = "other: String, other2: String, modifier: Modifier = Modifier",
                ),
            ),
            LintViolation(
                line = 5,
                col = 5,
                detail = ComposeParameterOrder.createErrorMessage(
                    currentOrder = "text: String = \"deffo\", modifier: Modifier = Modifier",
                    properOrder = "modifier: Modifier = Modifier, text: String = \"deffo\"",
                ),
            ),
            LintViolation(
                line = 8,
                col = 5,
                detail = ComposeParameterOrder.createErrorMessage(
                    currentOrder =
                    "modifier: Modifier = Modifier, text: String = \"123\", modifier2: Modifier = Modifier",
                    properOrder =
                    "modifier: Modifier = Modifier, modifier2: Modifier = Modifier, text: String = \"123\"",
                ),
            ),
            LintViolation(
                line = 11,
                col = 5,
                detail = ComposeParameterOrder.createErrorMessage(
                    currentOrder = "text: String = \"123\", modifier: Modifier = Modifier, lambda: () -> Unit",
                    properOrder = "modifier: Modifier = Modifier, text: String = \"123\", lambda: () -> Unit",
                ),
            ),
            LintViolation(
                line = 14,
                col = 5,
                detail = ComposeParameterOrder.createErrorMessage(
                    currentOrder =
                    "text1: String, m2: Modifier = Modifier, modifier: Modifier = Modifier, trailing: () -> Unit",
                    properOrder =
                    "text1: String, modifier: Modifier = Modifier, m2: Modifier = Modifier, trailing: () -> Unit",
                ),
            ),
        )
    }
}
