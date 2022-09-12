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
    fun `do not error on a regular composable`() {
        @Language("kotlin")
        val code = """
            @Composable
            fun TextHolder(text: String) {}
        """.trimIndent()

        val errors = rule.lint(code)
        assertThat(errors).isEmpty()
    }
}
