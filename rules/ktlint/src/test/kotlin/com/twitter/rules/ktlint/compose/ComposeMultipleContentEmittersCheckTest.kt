package com.twitter.rules.ktlint.compose

import com.pinterest.ktlint.core.LintError
import com.pinterest.ktlint.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

class ComposeNamingCheckTest {

    private val rule = ComposeNamingCheck()

    @Test
    fun `passes when a composable that returns values is lowercase`() {
        @Language("kotlin")
        val errors = rule.lint(
            """
                @Composable
                fun myComposable(): Something { }
            """.trimIndent()
        )
        assertThat(errors).isEmpty()
    }

    @Test
    fun `passes when a composable that returns nothing or Unit is uppercase`() {
        @Language("kotlin")
        val errors = rule.lint(
            """
                @Composable
                fun MyComposable() { }
                @Composable
                fun MyComposable(): Unit { }
            """.trimIndent()
        )
        assertThat(errors).isEmpty()
    }

    @Test
    fun `passes when a composable doesn't have a body block, is a property or a lambda`() {
        @Language("kotlin")
        val errors = rule.lint(
            """
                @Composable
                fun MyComposable() = Text("bleh")

                val composable: Something
                    @Composable get() { }

                val composable: Something
                    @Composable get() = OtherComposable()

                val whatever = @Composable { }
            """.trimIndent()
        )
        assertThat(errors).isEmpty()
    }

    @Test
    fun `errors when a composable returns a value and is capitalized`() {
        @Language("kotlin")
        val errors = rule.lint(
            """
                @Composable
                fun MyComposable(): Something { }
            """.trimIndent()
        )
        val expectedErrors = listOf(
            LintError(
                line = 2,
                col = 5,
                ruleId = "compose-naming-check",
                detail = ComposeNamingCheck.ComposablesThatReturnResultsShouldBeLowercase,
                canBeAutoCorrected = false
            ),
        )
        assertThat(errors).isEqualTo(expectedErrors)
    }

    @Test
    fun `errors when a composable returns nothing or Unit and is lowercase`() {
        @Language("kotlin")
        val errors = rule.lint(
            """
                @Composable
                fun myComposable() { }

                @Composable
                fun myComposable(): Unit { }
            """.trimIndent()
        )
        val expectedErrors = listOf(
            LintError(
                line = 2,
                col = 5,
                ruleId = "compose-naming-check",
                detail = ComposeNamingCheck.ComposablesThatDoNotReturnResultsShouldBeCapitalized,
                canBeAutoCorrected = false
            ),
            LintError(
                line = 5,
                col = 5,
                ruleId = "compose-naming-check",
                detail = ComposeNamingCheck.ComposablesThatDoNotReturnResultsShouldBeCapitalized,
                canBeAutoCorrected = false
            ),
        )
        assertThat(errors).isEqualTo(expectedErrors)
    }
}
