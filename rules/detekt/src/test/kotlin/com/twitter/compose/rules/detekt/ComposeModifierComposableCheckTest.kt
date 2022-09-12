// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules.detekt

import com.twitter.compose.rules.ComposeModifierComposable
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ComposeModifierComposableCheckTest {

    private val rule = ComposeModifierComposableCheck(Config.empty)

    @Test
    fun `errors when a composable Modifier extension is detected`() {
        @Language("kotlin")
        val code =
            """
                @Composable
                fun Modifier.something1(): Modifier { }
                @Composable
                fun Modifier.something2() = somethingElse()
            """.trimIndent()
        val errors = rule.lint(code)
        assertThat(errors).hasTextLocations("something1", "something2")
        assertThat(errors[0]).hasMessage(ComposeModifierComposable.ComposableModifier)
        assertThat(errors[1]).hasMessage(ComposeModifierComposable.ComposableModifier)
    }

    @Test
    fun `Do not error on regular @Composable functions`() {
        @Language("kotlin")
        val code = """
            @Composable
            fun MyComposable(text: String, modifier: Modifier = Modifier) {}
        """.trimIndent()

        val errors = rule.lint(code)
        Assertions.assertTrue(errors.isEmpty())
    }
}
