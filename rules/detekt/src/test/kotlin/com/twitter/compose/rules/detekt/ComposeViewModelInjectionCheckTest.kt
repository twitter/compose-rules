// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules.detekt

import com.twitter.compose.rules.ComposeViewModelInjection
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.intellij.lang.annotations.Language
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class ComposeViewModelInjectionCheckTest {

    private val rule = ComposeViewModelInjectionCheck(Config.empty)

    @ParameterizedTest
    @ValueSource(strings = ["viewModel", "weaverViewModel", "hiltViewModel", "injectedViewModel", "mavericksViewModel"])
    fun `passes when a weaverViewModel is used as a default param`(viewModel: String) {
        @Language("kotlin")
        val code =
            """
            @Composable
            fun MyComposable(
                modifier: Modifier,
                viewModel: MyVM = $viewModel(),
                viewModel2: MyVM = $viewModel(),
            ) { }
            """.trimIndent()
        val errors = rule.lint(code)
        assertThat(errors).isEmpty()
    }

    @ParameterizedTest
    @ValueSource(strings = ["viewModel", "weaverViewModel", "hiltViewModel", "injectedViewModel", "mavericksViewModel"])
    fun `overridden functions are ignored`(viewModel: String) {
        @Language("kotlin")
        val code =
            """
            @Composable
            override fun Content() {
                val viewModel = $viewModel<MyVM>()
            }
            """.trimIndent()
        val errors = rule.lint(code)
        assertThat(errors).isEmpty()
    }

    @ParameterizedTest
    @ValueSource(strings = ["viewModel", "weaverViewModel", "hiltViewModel", "injectedViewModel", "mavericksViewModel"])
    fun `errors when a weaverViewModel is used at the beginning of a Composable`(viewModel: String) {
        @Language("kotlin")
        val code =
            """
            @Composable
            fun MyComposable(modifier: Modifier) {
                val viewModel = $viewModel<MyVM>()
            }
            @Composable
            fun MyComposableNoParams() {
                val viewModel: MyVM = $viewModel()
            }
            @Composable
            fun MyComposableTrailingLambda(block: () -> Unit) {
                val viewModel: MyVM = $viewModel()
            }
            """.trimIndent()
        val errors = rule.lint(code)
        assertThat(errors).hasSize(3)
            .hasStartSourceLocations(
                SourceLocation(3, 9),
                SourceLocation(7, 9),
                SourceLocation(11, 9),
            )
        for (error in errors) {
            assertThat(error).hasMessage(ComposeViewModelInjection.errorMessage(viewModel))
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["viewModel", "weaverViewModel", "hiltViewModel", "injectedViewModel", "mavericksViewModel"])
    fun `errors when a weaverViewModel is used in different branches`(viewModel: String) {
        @Language("kotlin")
        val code =
            """
            @Composable
            fun MyComposable(modifier: Modifier) {
                if (blah) {
                    val viewModel = $viewModel<MyVM>()
                } else {
                    val viewModel: MyOtherVM = $viewModel()
                }
            }
            """.trimIndent()
        val errors = rule.lint(code)
        assertThat(errors).hasSize(2)
            .hasStartSourceLocations(
                SourceLocation(4, 13),
                SourceLocation(6, 13),
            )
        for (error in errors) {
            assertThat(error).hasMessage(ComposeViewModelInjection.errorMessage(viewModel))
        }
    }
}
