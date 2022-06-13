package com.twitter.rules.ktlint.compose

import com.pinterest.ktlint.core.LintError
import com.pinterest.ktlint.test.format
import com.pinterest.ktlint.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

class ComposeModifierMissingCheckTest {

    private val rule = ComposeModifierMissingCheck()

    @Test
    fun `errors when a Composable has a layout inside and it doesn't have a modifier`() {
        @Language("kotlin")
        val errors = rule.lint(
            """
                @Composable
                fun Something() {
                    Row {
                    }
                }
                @Composable
                fun Something() {
                    Column(modifier = Modifier.fillMaxSize()) {
                    }
                }
                @Composable
                fun Something(): Unit {
                    SomethingElse {
                        Box(modifier = Modifier.fillMaxSize()) {
                        }
                    }
                }
                @Composable
                fun Something(modifier: Modifier = Modifier) {
                    Row {
                        Text("Hi!")
                    }
                }
            """.trimIndent()
        )
        val expectedErrors = listOf(
            LintError(
                line = 2,
                col = 5,
                ruleId = "compose-modifier-check",
                detail = ComposeModifierMissingCheck.MissingModifierContentComposable,
                canBeAutoCorrected = false
            ),
            LintError(
                line = 7,
                col = 5,
                ruleId = "compose-modifier-check",
                detail = ComposeModifierMissingCheck.MissingModifierContentComposable,
                canBeAutoCorrected = false
            ),
            LintError(
                line = 12,
                col = 5,
                ruleId = "compose-modifier-check",
                detail = ComposeModifierMissingCheck.MissingModifierContentComposable,
                canBeAutoCorrected = false
            )
        )
        assertThat(errors).isEqualTo(expectedErrors)
    }

    @Test
    fun `errors when a Composable without modifiers has a Composable inside with a modifier`() {
        @Language("kotlin")
        val errors = rule.lint(
            """
                @Composable
                fun Something() {
                    Whatever(modifier = Modifier.fillMaxSize()) {
                    }
                }
                @Composable
                fun Something(): Unit {
                    SomethingElse {
                        Whatever(modifier = Modifier.fillMaxSize()) {
                        }
                    }
                }
            """.trimIndent()
        )
        val expectedErrors = listOf(
            LintError(
                line = 2,
                col = 5,
                ruleId = "compose-modifier-check",
                detail = ComposeModifierMissingCheck.MissingModifierContentComposable,
                canBeAutoCorrected = false
            ),
            LintError(
                line = 7,
                col = 5,
                ruleId = "compose-modifier-check",
                detail = ComposeModifierMissingCheck.MissingModifierContentComposable,
                canBeAutoCorrected = false
            )
        )
        assertThat(errors).isEqualTo(expectedErrors)
    }

    @Test
    fun `errors when a Composable has modifiers but without default values, and is able to auto fixing it`() {
        val check = rule

        @Language("kotlin")
        val composableCode = """
                @Composable
                fun Something(modifier: Modifier) {
                    Row(modifier = modifier) {
                    }
                }
        """.trimIndent()
        val errors = check.lint(composableCode)
        val expectedErrors = listOf(
            LintError(
                line = 2,
                col = 15,
                ruleId = "compose-modifier-check",
                detail = ComposeModifierMissingCheck.MissingModifierDefaultParam,
                canBeAutoCorrected = true
            )
        )
        assertThat(errors).isEqualTo(expectedErrors)
        val autoFixCode = check.format(composableCode)
        assertThat(autoFixCode).isEqualTo(
            """
                @Composable
                fun Something(modifier: Modifier = Modifier) {
                    Row(modifier = modifier) {
                    }
                }
            """.trimIndent()
        )
    }

    @Test
    fun `passes when a Composable has modifiers with defaults`() {
        @Language("kotlin")
        val errors = rule.lint(
            """
                @Composable
                fun Something(modifier: Modifier = Modifier) {
                    Row(modifier = modifier) {
                    }
                }
                @Composable
                fun Something(modifier: Modifier = Modifier.fillMaxSize()) {
                    Row(modifier = modifier) {
                    }
                }
                @Composable
                fun Something(modifier: Modifier = SomeOtherValueFromSomeConstant) {
                    Row(modifier = modifier) {
                    }
                }
            """.trimIndent()
        )
        assertThat(errors).isEmpty()
    }

    @Test
    fun `non-public visibility Composables are ignored`() {
        @Language("kotlin")
        val errors = rule.lint(
            """
                @Composable
                private fun Something() {
                    Row {
                    }
                }
                @Composable
                protected fun Something() {
                    Column(modifier = Modifier.fillMaxSize()) {
                    }
                }
                @Composable
                internal fun Something() {
                    SomethingElse {
                        Box(modifier = Modifier.fillMaxSize()) {
                        }
                    }
                }
                @Composable
                private fun Something() {
                    Whatever(modifier = Modifier.fillMaxSize()) {
                    }
                }
            """.trimIndent()
        )
        assertThat(errors).isEmpty()
    }

    @Test
    fun `interface Composables are ignored`() {
        @Language("kotlin")
        val errors = rule.lint(
            """
                interface MyInterface {
                    @Composable
                    fun Something() {
                        Row {
                        }
                    }

                    @Composable
                    fun Something() {
                        Column(modifier = Modifier.fillMaxSize()) {
                        }
                    }
                }
            """.trimIndent()
        )
        assertThat(errors).isEmpty()
    }

    @Test
    fun `overridden Composables are ignored`() {
        @Language("kotlin")
        val errors = rule.lint(
            """
                @Composable
                override fun Content() {
                    Row {
                    }
                }
                @Composable
                override fun TwitterContent() {
                    Row {
                    }
                }
                @Composable
                override fun ModalContent() {
                    Row {
                    }
                }
            """.trimIndent()
        )
        assertThat(errors).isEmpty()
    }

    @Test
    fun `Composables that return a type that is not Unit shouldn't be processed`() {
        @Language("kotlin")
        val errors = rule.lint(
            """
                @Composable
                fun Something(): Int {
                    Row {
                    }
                }
            """.trimIndent()
        )
        assertThat(errors).isEmpty()
    }
}
