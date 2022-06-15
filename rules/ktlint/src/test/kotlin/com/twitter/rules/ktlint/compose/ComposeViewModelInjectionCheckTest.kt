package com.twitter.rules.ktlint.compose

import com.pinterest.ktlint.core.LintError
import com.pinterest.ktlint.test.format
import com.pinterest.ktlint.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.intellij.lang.annotations.Language
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class ComposeViewModelInjectionCheckTest {

    private val rule = ComposeViewModelInjectionCheck()

    @ParameterizedTest
    @ValueSource(strings = ["viewModel", "weaverViewModel"])
    fun `passes when a weaverViewModel is used as a default param`(viewModel: String) {
        @Language("kotlin")
        val errors = rule.lint(
            """
            @Composable
            fun MyComposable(
                modifier: Modifier,
                viewModel: MyVM = $viewModel(),
                viewModel2: MyVM = $viewModel(),
            ) { }
            """.trimIndent()
        )
        assertThat(errors).isEmpty()
    }

    @ParameterizedTest
    @ValueSource(strings = ["viewModel", "weaverViewModel"])
    fun `overridden functions are ignored`(viewModel: String) {
        @Language("kotlin")
        val errors = rule.lint(
            """
            @Composable
            override fun Content() {
                val viewModel = $viewModel<MyVM>()
            }
            """.trimIndent()
        )
        assertThat(errors).isEmpty()
    }

    @ParameterizedTest
    @ValueSource(strings = ["viewModel", "weaverViewModel"])
    fun `errors when a weaverViewModel is used at the beginning of a Composable`(viewModel: String) {
        @Language("kotlin")
        val errors = rule.lint(
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
        )
        val expectedErrors = listOf(
            LintError(
                line = 3,
                col = 9,
                ruleId = "compose-vm-injection-check",
                detail = ComposeViewModelInjectionCheck.errorMessage(viewModel),
                canBeAutoCorrected = true
            ),
            LintError(
                line = 7,
                col = 9,
                ruleId = "compose-vm-injection-check",
                detail = ComposeViewModelInjectionCheck.errorMessage(viewModel),
                canBeAutoCorrected = true
            ),
            LintError(
                line = 11,
                col = 9,
                ruleId = "compose-vm-injection-check",
                detail = ComposeViewModelInjectionCheck.errorMessage(viewModel),
                canBeAutoCorrected = true
            )
        )
        assertThat(errors).isEqualTo(expectedErrors)
    }

    @ParameterizedTest
    @ValueSource(strings = ["viewModel", "weaverViewModel"])
    fun `errors when a weaverViewModel is used in different branches`(viewModel: String) {
        @Language("kotlin")
        val errors = rule.lint(
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
        )
        val expectedErrors = listOf(
            LintError(
                line = 4,
                col = 13,
                ruleId = "compose-vm-injection-check",
                detail = ComposeViewModelInjectionCheck.errorMessage(viewModel),
                canBeAutoCorrected = true
            ),
            LintError(
                line = 6,
                col = 13,
                ruleId = "compose-vm-injection-check",
                detail = ComposeViewModelInjectionCheck.errorMessage(viewModel),
                canBeAutoCorrected = true
            )
        )
        assertThat(errors).isEqualTo(expectedErrors)
    }

    @ParameterizedTest
    @ValueSource(strings = ["viewModel", "weaverViewModel"])
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
        val fixedCode = rule.format(badCode)
        assertThat(fixedCode).isEqualTo(expectedCode)
    }

    @ParameterizedTest
    @ValueSource(strings = ["viewModel", "weaverViewModel"])
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
        val fixedCode = rule.format(badCode)
        assertThat(fixedCode).isEqualTo(expectedCode)
    }

    @ParameterizedTest
    @ValueSource(strings = ["viewModel", "weaverViewModel"])
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
        val fixedCode = rule.format(badCode)
        assertThat(fixedCode).isEqualTo(expectedCode)
    }
}
