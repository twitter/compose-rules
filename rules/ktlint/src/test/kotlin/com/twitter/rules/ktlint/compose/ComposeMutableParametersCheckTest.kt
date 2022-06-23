package com.twitter.rules.ktlint.compose

import com.pinterest.ktlint.test.KtLintAssertThat.Companion.assertThat
import com.pinterest.ktlint.test.LintViolation
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

class ComposeMutableParametersCheckTest {

    private val mutableParamRuleAssertThat = ComposeMutableParametersCheck().assertThat()

    @Test
    fun `errors when a Composable has a mutable parameter`() {
        @Language("kotlin")
        val code =
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
        mutableParamRuleAssertThat(code).hasLintViolationsWithoutAutoCorrect(
            LintViolation(
                line = 2,
                col = 15,
                detail = ComposeMutableParametersCheck.MutableParameterInCompose,
            ),
            LintViolation(
                line = 4,
                col = 15,
                detail = ComposeMutableParametersCheck.MutableParameterInCompose,
            ),
            LintViolation(
                line = 6,
                col = 15,
                detail = ComposeMutableParametersCheck.MutableParameterInCompose,
            ),
            LintViolation(
                line = 8,
                col = 15,
                detail = ComposeMutableParametersCheck.MutableParameterInCompose,
            )
        )
    }

    @Test
    fun `no errors when a Composable has valid parameters`() {
        @Language("kotlin")
        val code =
            """
                @Composable
                fun Something(a: String, b: (Int) -> Unit) {}
                @Composable
                fun Something(a: State<String>) {}
            """.trimIndent()
        mutableParamRuleAssertThat(code).hasNoLintViolations()
    }
}
