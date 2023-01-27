// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules.detekt

import com.twitter.compose.rules.ComposePreviewPublic
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

class ComposePreviewPublicCheckTest {

    private val rule = ComposePreviewPublicCheck(Config.empty)

    @Test
    fun `passes for non-preview public composables`() {
        @Language("kotlin")
        val code =
            """
            @Composable
            fun MyComposable() { }
            """.trimIndent()
        val errors = rule.lint(code)
        assertThat(errors).isEmpty()
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
        val errors = rule.lint(code)
        assertThat(errors).isEmpty()
    }

    @Test
    fun `errors when a public preview composable is used when previewPublicOnlyIfParams is false`() {
        val config = TestConfig("previewPublicOnlyIfParams" to false)
        val ruleWithParams = ComposePreviewPublicCheck(config)

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
        val errors = ruleWithParams.lint(code)
        assertThat(errors).hasStartSourceLocations(
            SourceLocation(3, 5),
            SourceLocation(6, 5),
        )
        for (error in errors) {
            assertThat(error).hasMessage(ComposePreviewPublic.ComposablesPreviewShouldNotBePublic)
        }
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
        val errors = rule.lint(code)
        assertThat(errors).hasStartSourceLocations(
            SourceLocation(3, 5),
            SourceLocation(7, 5),
        )
        for (error in errors) {
            assertThat(error).hasMessage(ComposePreviewPublic.ComposablesPreviewShouldNotBePublic)
        }
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
        val errors = rule.lint(code)
        assertThat(errors).isEmpty()
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
        val errors = rule.lint(code)
        assertThat(errors).isEmpty()
    }
}
