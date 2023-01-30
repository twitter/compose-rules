// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules.ktlint

import com.pinterest.ktlint.test.KtLintAssertThat.Companion.assertThatRule
import com.pinterest.ktlint.test.LintViolation
import com.twitter.compose.rules.ComposeNaming
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

class ComposeNamingCheckTest {

    private val namingRuleAssertThat = assertThatRule { ComposeNamingCheck() }

    @Test
    fun `passes when a composable that returns values is lowercase`() {
        @Language("kotlin")
        val code =
            """
                @Composable
                fun myComposable(): Something { }
            """.trimIndent()
        namingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `passes when a composable that returns values is uppercase but allowed`() {
        @Language("kotlin")
        val code =
            """
                @Composable
                fun ProfilePresenter(): Something { }
            """.trimIndent()
        namingRuleAssertThat(code)
            .withEditorConfigOverride(
                allowedComposeNamingNames to ".*Presenter",
            )
            .hasNoLintViolations()
    }

    @Test
    fun `passes when a composable that returns nothing or Unit is uppercase`() {
        @Language("kotlin")
        val code =
            """
                @Composable
                fun MyComposable() { }
                @Composable
                fun MyComposable(): Unit { }
            """.trimIndent()
        namingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `passes when a composable doesn't have a body block, is a property or a lambda`() {
        @Language("kotlin")
        val code =
            """
                @Composable
                fun MyComposable() = Text("bleh")

                val composable: Something
                    @Composable get() { }

                val composable: Something
                    @Composable get() = OtherComposable()

                val whatever = @Composable { }
            """.trimIndent()
        namingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `errors when a composable returns a value and is capitalized`() {
        @Language("kotlin")
        val code =
            """
                @Composable
                fun MyComposable(): Something { }
            """.trimIndent()
        namingRuleAssertThat(code).hasLintViolationsWithoutAutoCorrect(
            LintViolation(
                line = 2,
                col = 5,
                detail = ComposeNaming.ComposablesThatReturnResultsShouldBeLowercase,
            ),
        )
    }

    @Test
    fun `errors when a composable returns nothing or Unit and is lowercase`() {
        @Language("kotlin")
        val code =
            """
                @Composable
                fun myComposable() { }

                @Composable
                fun myComposable(): Unit { }
            """.trimIndent()
        namingRuleAssertThat(code).hasLintViolationsWithoutAutoCorrect(
            LintViolation(
                line = 2,
                col = 5,
                detail = ComposeNaming.ComposablesThatDoNotReturnResultsShouldBeCapitalized,
            ),
            LintViolation(
                line = 5,
                col = 5,
                detail = ComposeNaming.ComposablesThatDoNotReturnResultsShouldBeCapitalized,
            ),
        )
    }

    @Test
    fun `passes when a composable returns nothing or Unit and is lowercase but has a receiver`() {
        @Language("kotlin")
        val code =
            """
                @Composable
                fun Potato.myComposable() { }

                @Composable
                fun Banana.myComposable(): Unit { }
            """.trimIndent()

        namingRuleAssertThat(code).hasNoLintViolations()
    }
}
