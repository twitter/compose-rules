package com.twitter.rules.ktlint.compose

import com.pinterest.ktlint.core.LintError
import com.pinterest.ktlint.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

class ComposeViewModelForwardingCheckTest {

    private val rule = ComposeViewModelForwardingCheck()

    @Test
    fun `allows the forwarding of ViewModels in overridden Composable functions`() {
        @Language("kotlin")
        val errors = rule.lint(
            """
            @Composable
            override fun Content() {
                val viewModel = weaverViewModel<MyVM>()
                AnotherComposable(viewModel)
            }
            """.trimIndent()
        )
        assertThat(errors).isEmpty()
    }

    @Test
    fun `allows the forwarding of ViewModels in interface Composable functions`() {
        @Language("kotlin")
        val errors = rule.lint(
            """
            interface MyInterface {
                @Composable
                fun Content() {
                    val viewModel = weaverViewModel<MyVM>()
                    AnotherComposable(viewModel)
                }
            }
            """.trimIndent()
        )
        assertThat(errors).isEmpty()
    }

    @Test
    fun `using state hoisting properly shouldn't be flagged`() {
        @Language("kotlin")
        val errors = rule.lint(
            """
            @Composable
            fun MyComposable(viewModel: MyViewModel = weaverViewModel()) {
                val state by viewModel.watchAsState()
                AnotherComposable(state, onAvatarClicked = { viewModel(AvatarClickedIntent) })
            }
            """.trimIndent()
        )
        assertThat(errors).isEmpty()
    }

    @Test
    fun `errors when a ViewModel is forwarded to another Composable`() {
        @Language("kotlin")
        val errors = rule.lint(
            """
            @Composable
            fun MyComposable(viewModel: MyViewModel) {
                AnotherComposable(viewModel)
            }
            """.trimIndent()
        )
        val expectedErrors = listOf(
            LintError(
                line = 3,
                col = 5,
                ruleId = "compose-vm-forwarding-check",
                detail = ComposeViewModelForwardingCheck.AvoidViewModelForwarding,
                canBeAutoCorrected = false
            )
        )
        assertThat(errors).isEqualTo(expectedErrors)
    }
}
