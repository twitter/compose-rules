package com.twitter.rules.ktlint.compose

import com.pinterest.ktlint.core.LintError
import com.pinterest.ktlint.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

class ComposeMutableParametersCheckTest {

    private val rule = ComposeMutableParametersCheck()

    @Test
    fun `errors when a Composable has a mutable parameter`() {
        @Language("kotlin")
        val errors = rule.lint(
            """
                @Composable
                fun Something(a: MutableState<String>) {}
                @Composable
                fun Something(a: ArrayList<String>) {}
                @Composable
                fun Something(a: HashSet<String>) {}
                @Composable
                fun Something(a: MutableMap<String, String>) {}
            """.trimIndent()
        )
        val expectedErrors = listOf(
            LintError(
                line = 2,
                col = 15,
                ruleId = "compose-mutable-params-check",
                detail = ComposeMutableParametersCheck.MutableParameterInCompose,
                canBeAutoCorrected = false
            ),
            LintError(
                line = 4,
                col = 15,
                ruleId = "compose-mutable-params-check",
                detail = ComposeMutableParametersCheck.MutableParameterInCompose,
                canBeAutoCorrected = false
            ),
            LintError(
                line = 6,
                col = 15,
                ruleId = "compose-mutable-params-check",
                detail = ComposeMutableParametersCheck.MutableParameterInCompose,
                canBeAutoCorrected = false
            ),
            LintError(
                line = 8,
                col = 15,
                ruleId = "compose-mutable-params-check",
                detail = ComposeMutableParametersCheck.MutableParameterInCompose,
                canBeAutoCorrected = false
            )
        )
        assertThat(errors).isEqualTo(expectedErrors)
    }

    @Test
    fun `no errors when a Composable has valid parameters`() {
        @Language("kotlin")
        val errors = rule.lint(
            """
                @Composable
                fun Something(a: String, b: (Int) -> Unit) {}
                @Composable
                fun Something(a: State<String>) {}
            """.trimIndent()
        )
        assertThat(errors).isEmpty()
    }
}
