// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules.detekt

import com.twitter.compose.rules.ComposeMultipleContentEmitters
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

class ComposeMultipleContentEmittersCheckTest {

    private val testConfig = TestConfig(
        "contentEmitters" to listOf("Potato", "Banana"),
    )
    private val rule = ComposeMultipleContentEmittersCheck(testConfig)

    @Test
    fun `passes when only one item emits up at the top level`() {
        @Language("kotlin")
        val code =
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
        val errors = rule.lint(code)
        assertThat(errors).isEmpty()
    }

    @Test
    fun `passes when the composable is an extension function`() {
        @Language("kotlin")
        val code =
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
        val errors = rule.lint(code)
        assertThat(errors).isEmpty()
    }

    @Test
    fun `errors when a Composable function has more than one UI emitter at the top level`() {
        @Language("kotlin")
        val code =
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
        val errors = rule.lint(code)
        assertThat(errors).hasSize(2)
            .hasStartSourceLocations(
                SourceLocation(2, 5),
                SourceLocation(7, 5),
            )
        for (error in errors) {
            assertThat(error).hasMessage(ComposeMultipleContentEmitters.MultipleContentEmittersDetected)
        }
    }

    @Test
    fun `errors when a Composable function has more than one indirect UI emitter at the top level`() {
        @Language("kotlin")
        val code =
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
                    Potato()
                }
                @Composable
                fun Something4() {
                    Banana()
                }
                @Composable
                fun Something5() {
                    Something3()
                    Something4()
                }
            """.trimIndent()
        val errors = rule.lint(code)
        assertThat(errors).hasSize(2)
            .hasStartSourceLocations(
                SourceLocation(6, 5),
                SourceLocation(19, 5),
            )
        for (error in errors) {
            assertThat(error).hasMessage(ComposeMultipleContentEmitters.MultipleContentEmittersDetected)
        }
    }

    @Test
    fun `make sure to not report twice the same composable`() {
        @Language("kotlin")
        val code =
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
        val errors = rule.lint(code)
        assertThat(errors).hasSize(1)
            .hasStartSourceLocation(2, 5)
        assertThat(errors.first()).hasMessage(ComposeMultipleContentEmitters.MultipleContentEmittersDetected)
    }
}
