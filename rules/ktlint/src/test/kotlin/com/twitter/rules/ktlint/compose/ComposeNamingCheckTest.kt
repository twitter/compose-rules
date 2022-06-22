package com.twitter.rules.ktlint.compose

import com.pinterest.ktlint.test.KtLintAssertThat.Companion.assertThat
import com.pinterest.ktlint.test.LintViolation
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

class ComposeNamingCheckTest {

    private val namingRuleAssertThat = ComposeNamingCheck().assertThat()

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
                detail = ComposeNamingCheck.ComposablesThatReturnResultsShouldBeLowercase,
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
                detail = ComposeNamingCheck.ComposablesThatDoNotReturnResultsShouldBeCapitalized,
            ),
            LintViolation(
                line = 5,
                col = 5,
                detail = ComposeNamingCheck.ComposablesThatDoNotReturnResultsShouldBeCapitalized,
            ),
        )
    }
}
