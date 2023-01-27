// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules.ktlint

import com.pinterest.ktlint.test.KtLintAssertThat.Companion.assertThatRule
import com.pinterest.ktlint.test.LintViolation
import com.twitter.compose.rules.ComposeModifierMissing
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

class ComposeModifierMissingCheckTest {

    private val modifierRuleAssertThat = assertThatRule { ComposeModifierMissingCheck() }

    @Test
    fun `errors when a Composable has a layout inside and it doesn't have a modifier`() {
        @Language("kotlin")
        val code =
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

        modifierRuleAssertThat(code).hasLintViolationsWithoutAutoCorrect(
            LintViolation(
                line = 2,
                col = 5,
                detail = ComposeModifierMissing.MissingModifierContentComposable,
            ),
            LintViolation(
                line = 7,
                col = 5,
                detail = ComposeModifierMissing.MissingModifierContentComposable,
            ),
            LintViolation(
                line = 12,
                col = 5,
                detail = ComposeModifierMissing.MissingModifierContentComposable,
            ),
        )
    }

    @Test
    fun `errors when a Composable without modifiers has a Composable inside with a modifier`() {
        @Language("kotlin")
        val code =
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

        modifierRuleAssertThat(code).hasLintViolationsWithoutAutoCorrect(
            LintViolation(
                line = 2,
                col = 5,
                detail = ComposeModifierMissing.MissingModifierContentComposable,
            ),
            LintViolation(
                line = 7,
                col = 5,
                detail = ComposeModifierMissing.MissingModifierContentComposable,
            ),
        )
    }

    @Test
    fun `non-public visibility Composables are ignored`() {
        @Language("kotlin")
        val code =
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
        modifierRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `interface Composables are ignored`() {
        @Language("kotlin")
        val code =
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
        modifierRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `overridden Composables are ignored`() {
        @Language("kotlin")
        val code =
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
        modifierRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `Composables that return a type that is not Unit shouldn't be processed`() {
        @Language("kotlin")
        val code =
            """
                @Composable
                fun Something(): Int {
                    Row {
                    }
                }
            """.trimIndent()
        modifierRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `Composables with @Preview are ignored`() {
        @Language("kotlin")
        val code =
            """
                @Preview
                @Composable
                fun Something() {
                    Row {
                    }
                }
                @Preview
                @Composable
                fun Something() {
                    Column(modifier = Modifier.fillMaxSize()) {
                    }
                }
                @Preview
                @Composable
                fun Something(): Unit {
                    SomethingElse {
                        Box(modifier = Modifier.fillMaxSize()) {
                        }
                    }
                }
            """.trimIndent()

        modifierRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `non content emitting root composables are ignored`() {
        @Language("kotlin")
        val code =
            """
                @Composable
                fun MyDialog() {
                  AlertDialog(
                    onDismissRequest = { /*TODO*/ },
                    buttons = { Text(text = "Button") },
                    text = { Text(text = "Body") },
                  )
                }
            """.trimIndent()

        modifierRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `non content emitter with content emitter not ignored`() {
        @Language("kotlin")
        val code =
            """
                @Composable
                fun MyDialog() {
                  Text(text = "Unicorn")

                  AlertDialog(
                    onDismissRequest = { /*TODO*/ },
                    buttons = { Text(text = "Button") },
                    text = { Text(text = "Body") },
                  )
                }
            """.trimIndent()

        modifierRuleAssertThat(code).hasLintViolationsWithoutAutoCorrect(
            LintViolation(
                line = 2,
                col = 5,
                detail = ComposeModifierMissing.MissingModifierContentComposable,
            ),
        )
    }
}
