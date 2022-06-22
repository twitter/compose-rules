package com.twitter.rules.ktlint.compose

import com.pinterest.ktlint.test.KtLintAssertThat.Companion.assertThat
import com.pinterest.ktlint.test.LintViolation
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

class ComposeViewModelForwardingCheckTest {

    private val forwardingRuleAssertThat = ComposeViewModelForwardingCheck().assertThat()

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
                detail = ComposeViewModelForwardingCheck.AvoidViewModelForwarding,
            )
        )
    }
}
