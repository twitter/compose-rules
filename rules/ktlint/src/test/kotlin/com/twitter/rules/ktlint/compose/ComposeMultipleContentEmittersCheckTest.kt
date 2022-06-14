package com.twitter.rules.ktlint.compose

import com.pinterest.ktlint.core.LintError
import com.pinterest.ktlint.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

class ComposeMultipleContentEmittersCheckTest {

    private val rule = ComposeMultipleContentEmittersCheck()

    @Test
    fun `passes when only one item emits up at the top level`() {
        @Language("kotlin")
        val errors = rule.lint(
            """
                @Composable
                fun Something() {
                    val something = rememberWhatever()
                    Column {
                        Text("Hi")
                        Text("Hola")
                    }
                    LaunchedEffect(Unit) {
                    }
                }
            """.trimIndent()
        )
        assertThat(errors).isEmpty()
    }

    @Test
    fun `passes when the composable is an extension function`() {
        @Language("kotlin")
        val errors = rule.lint(
            """
                @Composable
                fun ColumnScope.Something() {
                    Text("Hi")
                    Text("Hola")
                }
                @Composable
                fun RowScope.Something() {
                    Spacer16()
                    Text("Hola")
                }
            """.trimIndent()
        )
        assertThat(errors).isEmpty()
    }

    @Test
    fun `errors when a Composable function has more than one UI emitter at the top level`() {
        @Language("kotlin")
        val errors = rule.lint(
            """
                @Composable
                fun Something() {
                    Text("Hi")
                    Text("Hola")
                }
                @Composable
                fun Something() {
                    Spacer16()
                    Text("Hola")
                }
            """.trimIndent()
        )
        val expectedErrors = listOf(
            LintError(
                line = 2,
                col = 5,
                ruleId = "compose-multiple-emitters-check",
                detail = ComposeMultipleContentEmittersCheck.MultipleContentEmittersDetected,
                canBeAutoCorrected = false
            ),
            LintError(
                line = 7,
                col = 5,
                ruleId = "compose-multiple-emitters-check",
                detail = ComposeMultipleContentEmittersCheck.MultipleContentEmittersDetected,
                canBeAutoCorrected = false
            ),
        )
        assertThat(errors).isEqualTo(expectedErrors)
    }

    @Test
    fun `errors when a Composable function has more than one indirect UI emitter at the top level`() {
        @Language("kotlin")
        val errors = rule.lint(
            """
                @Composable
                fun Something1() {
                    Something2()
                }
                @Composable
                fun Something2() {
                    Text("Hola")
                    Something3()
                }
                @Composable
                fun Something3() {
                    Text("Hi")
                }
                @Composable
                fun Something4() {
                    Text("Alo")
                }
                @Composable
                fun Something5() {
                    Something3()
                    Something4()
                }
            """.trimIndent()
        )
        val expectedErrors = listOf(
            LintError(
                line = 6,
                col = 5,
                ruleId = "compose-multiple-emitters-check",
                detail = ComposeMultipleContentEmittersCheck.MultipleContentEmittersDetected,
                canBeAutoCorrected = false
            ),
            LintError(
                line = 19,
                col = 5,
                ruleId = "compose-multiple-emitters-check",
                detail = ComposeMultipleContentEmittersCheck.MultipleContentEmittersDetected,
                canBeAutoCorrected = false
            ),
        )
        assertThat(errors).isEqualTo(expectedErrors)
    }

    @Test
    fun `make sure to not report twice the same composable`() {
        @Language("kotlin")
        val errors = rule.lint(
            """
                @Composable
                fun Something() {
                    Text("Hi")
                    Text("Hola")
                    Something2()
                }
                @Composable
                fun Something2() {
                    Text("Alo")
                }
            """.trimIndent()
        )
        val expectedErrors = listOf(
            LintError(
                line = 2,
                col = 5,
                ruleId = "compose-multiple-emitters-check",
                detail = ComposeMultipleContentEmittersCheck.MultipleContentEmittersDetected,
                canBeAutoCorrected = false
            ),
        )
        assertThat(errors).isEqualTo(expectedErrors)
    }

    @Test
    fun `error out when detecting a content emitting composable that returns something other than unit`() {
        @Language("kotlin")
        val errors = rule.lint(
            """
                @Composable
                fun Something(): String { // This one emits content directly and should fail
                    Text("Hi")
                    return "Potato"
                }
                @Composable
                fun Something2(): WhateverState { // This one emits content indirectly and should fail too
                    Something3()
                    return remember { WhateverState() }
                }
                @Composable
                fun Something3() { // This one is fine but calling it should make Something2 fail
                    HorizonIcon(icon = HorizonIcon.Arrow)
                }
            """.trimIndent()
        )
        val expectedErrors = listOf(
            LintError(
                line = 2,
                col = 5,
                ruleId = "compose-multiple-emitters-check",
                detail = ComposeMultipleContentEmittersCheck.ContentEmitterReturningValuesToo,
                canBeAutoCorrected = false
            ),
            LintError(
                line = 7,
                col = 5,
                ruleId = "compose-multiple-emitters-check",
                detail = ComposeMultipleContentEmittersCheck.ContentEmitterReturningValuesToo,
                canBeAutoCorrected = false
            ),
        )
        assertThat(errors).isEqualTo(expectedErrors)
    }
}
