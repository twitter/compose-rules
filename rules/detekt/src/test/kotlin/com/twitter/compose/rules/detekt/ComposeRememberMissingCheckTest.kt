// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules.detekt

import com.twitter.compose.rules.ComposeRememberMissing
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

class ComposeRememberMissingCheckTest {

    private val rule = ComposeRememberMissingCheck(Config.empty)

    @Test
    fun `passes when a non-remembered mutableStateOf is used outside of a Composable`() {
        @Language("kotlin")
        val code =
            """
                val msof = mutableStateOf("X")
            """.trimIndent()
        val errors = rule.lint(code)
        assertThat(errors).isEmpty()
    }

    @Test
    fun `errors when a non-remembered mutableStateOf is used in a Composable`() {
        @Language("kotlin")
        val code =
            """
                @Composable
                fun MyComposable() {
                    val something = mutableStateOf("X")
                }
                @Composable
                fun MyComposable(something: State<String> = mutableStateOf("X")) {
                }
            """.trimIndent()
        val errors = rule.lint(code)
        assertThat(errors).hasSize(2)
            .hasStartSourceLocations(
                SourceLocation(3, 21),
                SourceLocation(6, 45),
            )
        for (error in errors) {
            assertThat(error).hasMessage(ComposeRememberMissing.MutableStateOfNotRemembered)
        }
    }

    @Test
    fun `passes when a remembered mutableStateOf is used in a Composable`() {
        @Language("kotlin")
        val code =
            """
                @Composable
                fun MyComposable(
                    something: State<String> = remember { mutableStateOf("X") }
                ) {
                    val something = remember { mutableStateOf("X") }
                    val something2 by remember { mutableStateOf("Y") }
                }
            """.trimIndent()
        val errors = rule.lint(code)
        assertThat(errors).isEmpty()
    }

    @Test
    fun `passes when a rememberSaveable mutableStateOf is used in a Composable`() {
        @Language("kotlin")
        val code =
            """
                @Composable
                fun MyComposable(
                    something: State<String> = rememberSaveable { mutableStateOf("X") }
                ) {
                    val something = rememberSaveable { mutableStateOf("X") }
                    val something2 by rememberSaveable { mutableStateOf("Y") }
                }
            """.trimIndent()
        val errors = rule.lint(code)
        assertThat(errors).isEmpty()
    }

    @Test
    fun `passes when a non-remembered derivedStateOf is used outside of a Composable`() {
        @Language("kotlin")
        val code =
            """
                val dsof = derivedStateOf("X")
            """.trimIndent()
        val errors = rule.lint(code)
        assertThat(errors).isEmpty()
    }

    @Test
    fun `errors when a non-remembered derivedStateOf is used in a Composable`() {
        @Language("kotlin")
        val code =
            """
                @Composable
                fun MyComposable() {
                    val something = derivedStateOf { "X" }
                }
                @Composable
                fun MyComposable(something: State<String> = derivedStateOf { "X" }) {
                }
            """.trimIndent()
        val errors = rule.lint(code)
        assertThat(errors).hasSize(2)
            .hasStartSourceLocations(
                SourceLocation(3, 21),
                SourceLocation(6, 45),
            )
        for (error in errors) {
            assertThat(error).hasMessage(ComposeRememberMissing.DerivedStateOfNotRemembered)
        }
    }

    @Test
    fun `passes when a remembered derivedStateOf is used in a Composable`() {
        @Language("kotlin")
        val code =
            """
                @Composable
                fun MyComposable(
                    something: State<String> = remember { derivedStateOf { "X" } }
                ) {
                    val something = remember { derivedStateOf { "X" } }
                    val something2 by remember { derivedStateOf { "Y" } }
                }
            """.trimIndent()
        val errors = rule.lint(code)
        assertThat(errors).isEmpty()
    }
}
