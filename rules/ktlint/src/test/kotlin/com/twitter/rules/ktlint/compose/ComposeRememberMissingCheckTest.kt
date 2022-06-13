package com.twitter.rules.ktlint.compose

import com.pinterest.ktlint.core.LintError
import com.pinterest.ktlint.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

class ComposeRememberMissingCheckTest {

    private val rule = ComposeRememberMissingCheck()

    @Test
    fun `passes when a non-remembered mutableStateOf is used outside of a Composable`() {
        @Language("kotlin")
        val errors = rule.lint(
            """
                val msof = mutableStateOf("X")
            """.trimIndent()
        )
        assertThat(errors).isEmpty()
    }

    @Test
    fun `errors when a non-remembered mutableStateOf is used in a Composable`() {
        @Language("kotlin")
        val errors = rule.lint(
            """
                @Composable
                fun MyComposable() {
                    val something = mutableStateOf("X")
                }
                @Composable
                fun MyComposable(something: State<String> = mutableStateOf("X")) {
                }
            """.trimIndent()
        )
        val expectedErrors = listOf(
            LintError(
                line = 3,
                col = 21,
                ruleId = "compose-remember-missing-check",
                detail = ComposeRememberMissingCheck.MutableStateOfNotRemembered,
                canBeAutoCorrected = false
            ),
            LintError(
                line = 6,
                col = 45,
                ruleId = "compose-remember-missing-check",
                detail = ComposeRememberMissingCheck.MutableStateOfNotRemembered,
                canBeAutoCorrected = false
            ),
        )
        assertThat(errors).isEqualTo(expectedErrors)
    }

    @Test
    fun `passes when a remembered mutableStateOf is used in a Composable`() {
        @Language("kotlin")
        val errors = rule.lint(
            """
                @Composable
                fun MyComposable(
                    something: State<String> = remember { mutableStateOf("X") }
                ) {
                    val something = remember { mutableStateOf("X") }
                    val something2 by remember { mutableStateOf("Y") }
                }
            """.trimIndent()
        )
        assertThat(errors).isEmpty()
    }

    @Test
    fun `passes when a rememberSaveable mutableStateOf is used in a Composable`() {
        @Language("kotlin")
        val errors = rule.lint(
            """
                @Composable
                fun MyComposable(
                    something: State<String> = rememberSaveable { mutableStateOf("X") }
                ) {
                    val something = rememberSaveable { mutableStateOf("X") }
                    val something2 by rememberSaveable { mutableStateOf("Y") }
                }
            """.trimIndent()
        )
        assertThat(errors).isEmpty()
    }

    @Test
    fun `passes when a non-remembered derivedStateOf is used outside of a Composable`() {
        @Language("kotlin")
        val errors = rule.lint(
            """
                val dsof = derivedStateOf("X")
            """.trimIndent()
        )
        assertThat(errors).isEmpty()
    }

    @Test
    fun `errors when a non-remembered derivedStateOf is used in a Composable`() {
        @Language("kotlin")
        val errors = rule.lint(
            """
                @Composable
                fun MyComposable() {
                    val something = derivedStateOf { "X" }
                }
                @Composable
                fun MyComposable(something: State<String> = derivedStateOf { "X" }) {
                }
            """.trimIndent()
        )
        val expectedErrors = listOf(
            LintError(
                line = 3,
                col = 21,
                ruleId = "compose-remember-missing-check",
                detail = ComposeRememberMissingCheck.DerivedStateOfNotRemembered,
                canBeAutoCorrected = false
            ),
            LintError(
                line = 6,
                col = 45,
                ruleId = "compose-remember-missing-check",
                detail = ComposeRememberMissingCheck.DerivedStateOfNotRemembered,
                canBeAutoCorrected = false
            ),
        )
        assertThat(errors).isEqualTo(expectedErrors)
    }

    @Test
    fun `passes when a remembered derivedStateOf is used in a Composable`() {
        @Language("kotlin")
        val errors = rule.lint(
            """
                @Composable
                fun MyComposable(
                    something: State<String> = remember { derivedStateOf { "X" } }
                ) {
                    val something = remember { derivedStateOf { "X" } }
                    val something2 by remember { derivedStateOf { "Y" } }
                }
            """.trimIndent()
        )
        assertThat(errors).isEmpty()
    }
}
