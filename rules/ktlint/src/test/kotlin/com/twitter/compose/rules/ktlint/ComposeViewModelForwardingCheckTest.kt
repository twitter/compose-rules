// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules.ktlint

import com.pinterest.ktlint.test.KtLintAssertThat.Companion.assertThatRule
import com.pinterest.ktlint.test.LintViolation
import com.twitter.compose.rules.ComposeViewModelForwarding
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

class ComposeViewModelForwardingCheckTest {

    private val forwardingRuleAssertThat = assertThatRule { ComposeViewModelForwardingCheck() }

    @Test
    fun `allows the forwarding of ViewModels in overridden Composable functions`() {
        @Language("kotlin")
        val code =
            """
            @Composable
            override fun Content() {
                val viewModel = weaverViewModel<MyVM>()
                AnotherComposable(viewModel)
            }
            """.trimIndent()
        forwardingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `allows the forwarding of ViewModels in interface Composable functions`() {
        @Language("kotlin")
        val code =
            """
            interface MyInterface {
                @Composable
                fun Content() {
                    val viewModel = weaverViewModel<MyVM>()
                    AnotherComposable(viewModel)
                }
            }
            """.trimIndent()
        forwardingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `using state hoisting properly shouldn't be flagged`() {
        @Language("kotlin")
        val code =
            """
            @Composable
            fun MyComposable(viewModel: MyViewModel = weaverViewModel()) {
                val state by viewModel.watchAsState()
                AnotherComposable(state, onAvatarClicked = { viewModel(AvatarClickedIntent) })
            }
            """.trimIndent()
        forwardingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `errors when a ViewModel is forwarded to another Composable`() {
        @Language("kotlin")
        val code =
            """
            @Composable
            fun MyComposable(viewModel: MyViewModel) {
                AnotherComposable(viewModel)
            }
            """.trimIndent()
        forwardingRuleAssertThat(code).hasLintViolationsWithoutAutoCorrect(
            LintViolation(
                line = 3,
                col = 5,
                detail = ComposeViewModelForwarding.AvoidViewModelForwarding,
            ),
        )
    }

    @Test
    fun `allows the forwarding of ViewModels that are used as keys`() {
        @Language("kotlin")
        val code =
            """
            @Composable
            fun Content() {
                val viewModel = weaverViewModel<MyVM>()
                key(viewModel) { }
                val x = remember(viewModel) { "ABC" }
                LaunchedEffect(viewModel) { }
            }
            """.trimIndent()
        forwardingRuleAssertThat(code).hasNoLintViolations()
    }
}
