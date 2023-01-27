// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules.ktlint

import com.pinterest.ktlint.test.KtLintAssertThat.Companion.assertThatRule
import com.pinterest.ktlint.test.LintViolation
import com.twitter.compose.rules.ComposeViewModelInjection
import org.intellij.lang.annotations.Language
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class ComposeViewModelInjectionCheckTest {

    private val injectionRuleAssertThat = assertThatRule { ComposeViewModelInjectionCheck() }

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
        injectionRuleAssertThat(code).hasNoLintViolations()
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
        injectionRuleAssertThat(code).hasNoLintViolations()
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
        injectionRuleAssertThat(code).hasLintViolations(
            LintViolation(
                line = 3,
                col = 9,
                detail = ComposeViewModelInjection.errorMessage(viewModel),
            ),
            LintViolation(
                line = 7,
                col = 9,
                detail = ComposeViewModelInjection.errorMessage(viewModel),
            ),
            LintViolation(
                line = 11,
                col = 9,
                detail = ComposeViewModelInjection.errorMessage(viewModel),
            ),
        )
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
        injectionRuleAssertThat(code).hasLintViolations(
            LintViolation(
                line = 4,
                col = 13,
                detail = ComposeViewModelInjection.errorMessage(viewModel),
            ),
            LintViolation(
                line = 6,
                col = 13,
                detail = ComposeViewModelInjection.errorMessage(viewModel),
            ),
        )
    }

    @ParameterizedTest
    @ValueSource(strings = ["viewModel", "weaverViewModel", "hiltViewModel", "injectedViewModel", "mavericksViewModel"])
    fun `fix no args composable function adds the code inside the parentheses`(viewModel: String) {
        @Language("kotlin")
        val badCode = """
            @Composable
            fun MyComposableNoParams() {
                val viewModel: MyVM = $viewModel()
            }
            @Composable
            fun MyComposableNoParams() {
                val viewModel: MyVM = $viewModel(named = "meh")
            }
        """.trimIndent()

        @Language("kotlin")
        val expectedCode = """
            @Composable
            fun MyComposableNoParams(viewModel: MyVM = $viewModel()) {
            }
            @Composable
            fun MyComposableNoParams(viewModel: MyVM = $viewModel(named = "meh")) {
            }
        """.trimIndent()

        injectionRuleAssertThat(badCode).isFormattedAs(expectedCode)
    }

    @ParameterizedTest
    @ValueSource(strings = ["viewModel", "weaverViewModel", "hiltViewModel", "injectedViewModel", "mavericksViewModel"])
    fun `fix normal args composable function adds the new code at the end`(viewModel: String) {
        @Language("kotlin")
        val badCode = """
            @Composable
            fun MyComposable(modifier: Modifier = Modifier) {
                val viewModel: MyVM = $viewModel()
            }
            @Composable
            fun MyComposable(modifier: Modifier = Modifier,) {
                val viewModel: MyVM = $viewModel()
            }
        """.trimIndent()

        @Language("kotlin")
        val expectedCode = """
            @Composable
            fun MyComposable(modifier: Modifier = Modifier,viewModel: MyVM = $viewModel()) {
            }
            @Composable
            fun MyComposable(modifier: Modifier = Modifier,viewModel: MyVM = $viewModel(),) {
            }
        """.trimIndent()
        injectionRuleAssertThat(badCode).isFormattedAs(expectedCode)
    }

    @ParameterizedTest
    @ValueSource(strings = ["viewModel", "weaverViewModel", "hiltViewModel", "injectedViewModel", "mavericksViewModel"])
    fun `fix trailing lambda args composable function adds the new code before the trailing lambda`(viewModel: String) {
        @Language("kotlin")
        val badCode = """
            @Composable
            fun MyComposableTrailingLambda(block: () -> Unit) {
                val viewModel: MyVM = $viewModel()
            }
            @Composable
            fun MyComposableTrailingLambda(text: String, block: () -> Unit) {
                val viewModel: MyVM = $viewModel()
            }
            @Composable
            fun MyComposableTrailingLambda(
                text: String,
                block: () -> Unit
            ) {
                val viewModel: MyVM = $viewModel()
            }
        """.trimIndent()

        @Language("kotlin")
        val expectedCode = """
            @Composable
            fun MyComposableTrailingLambda(viewModel: MyVM = $viewModel(), block: () -> Unit) {
            }
            @Composable
            fun MyComposableTrailingLambda(text: String, viewModel: MyVM = $viewModel(), block: () -> Unit) {
            }
            @Composable
            fun MyComposableTrailingLambda(
                text: String, viewModel: MyVM = $viewModel(),
                block: () -> Unit
            ) {
            }
        """.trimIndent()
        injectionRuleAssertThat(badCode).isFormattedAs(expectedCode)
    }
}
