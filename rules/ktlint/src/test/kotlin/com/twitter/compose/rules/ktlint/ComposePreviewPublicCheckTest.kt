// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules.ktlint

import com.pinterest.ktlint.test.KtLintAssertThat.Companion.assertThatRule
import com.pinterest.ktlint.test.LintViolation
import com.twitter.compose.rules.ComposePreviewPublic
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

class ComposePreviewPublicCheckTest {

    private val ruleAssertThat = assertThatRule { ComposePreviewPublicCheck() }

    @Test
    fun `passes for non-preview public composables`() {
        @Language("kotlin")
        val code =
            """
            @Composable
            fun MyComposable() { }
            """.trimIndent()
        ruleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `passes for preview public composables that don't have preview params`() {
        @Language("kotlin")
        val code =
            """
            @Preview
            @Composable
            fun MyComposable() { }
            @CombinedPreviews
            @Composable
            fun MyComposable() { }
            """.trimIndent()
        ruleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `errors when a public preview composable uses preview params`() {
        @Language("kotlin")
        val code =
            """
            @Preview
            @Composable
            fun MyComposable(@PreviewParameter(User::class) user: User) {
            }
            @CombinedPreviews
            @Composable
            fun MyComposable(@PreviewParameter(User::class) user: User) {
            }
            """.trimIndent()
        ruleAssertThat(code).hasLintViolations(
            LintViolation(
                line = 3,
                col = 5,
                detail = ComposePreviewPublic.ComposablesPreviewShouldNotBePublic,
            ),
            LintViolation(
                line = 7,
                col = 5,
                detail = ComposePreviewPublic.ComposablesPreviewShouldNotBePublic,
            ),
        )
    }

    @Test
    fun `errors when a public preview composable is used when previewPublicOnlyIfParams is false`() {
        @Language("kotlin")
        val code =
            """
            @Preview
            @Composable
            fun MyComposable() { }
            @CombinedPreviews
            @Composable
            fun MyComposable() { }
            """.trimIndent()
        ruleAssertThat(code)
            .withEditorConfigOverride(previewPublicOnlyIfParams to false)
            .hasLintViolations(
                LintViolation(
                    line = 3,
                    col = 5,
                    detail = ComposePreviewPublic.ComposablesPreviewShouldNotBePublic,
                ),
                LintViolation(
                    line = 6,
                    col = 5,
                    detail = ComposePreviewPublic.ComposablesPreviewShouldNotBePublic,
                ),
            )
    }

    @Test
    fun `passes when a non-public preview composable uses preview params`() {
        @Language("kotlin")
        val code =
            """
            @Preview
            @Composable
            private fun MyComposable(@PreviewParameter(User::class) user: User) {
            }
            @CombinedPreviews
            @Composable
            internal fun MyComposable(@PreviewParameter(User::class) user: User) {
            }
            """.trimIndent()
        ruleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `autofix makes private the public preview`() {
        @Language("kotlin")
        val badCode = """
            @Preview
            @Composable
            fun MyComposable(@PreviewParameter(User::class) user: User) {
            }
            @CombinedPreviews
            @Composable
            fun MyComposable(@PreviewParameter(User::class) user: User) {
            }
        """.trimIndent()

        @Language("kotlin")
        val expectedCode = """
            @Preview
            @Composable
            private fun MyComposable(@PreviewParameter(User::class) user: User) {
            }
            @CombinedPreviews
            @Composable
            private fun MyComposable(@PreviewParameter(User::class) user: User) {
            }
        """.trimIndent()
        ruleAssertThat(badCode).isFormattedAs(expectedCode)
    }

    @Test
    fun `passes when a private preview composable uses preview params`() {
        @Language("kotlin")
        val code =
            """
            @Preview
            @Composable
            private fun MyComposable(@PreviewParameter(User::class) user: User) {
            }
            @CombinedPreviews
            @Composable
            private fun MyComposable(@PreviewParameter(User::class) user: User) {
            }
            """.trimIndent()
        ruleAssertThat(code).hasNoLintViolations()
    }
}
