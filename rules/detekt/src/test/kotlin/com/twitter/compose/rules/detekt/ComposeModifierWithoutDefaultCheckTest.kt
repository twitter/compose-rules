// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules.detekt

import com.twitter.compose.rules.ComposeModifierWithoutDefault
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

class ComposeModifierWithoutDefaultCheckTest {

    private val rule = ComposeModifierWithoutDefaultCheck(Config.empty)

    @Test
    fun `errors when a Composable has modifiers but without default values`() {
        @Language("kotlin")
        val composableCode = """
                @Composable
                fun Something(modifier: Modifier) { }
                @Composable
                fun Something(modifier: Modifier = Modifier, modifier2: Modifier) { }
        """.trimIndent()

        val errors = rule.lint(composableCode)
        assertThat(errors).hasSourceLocations(
            SourceLocation(2, 15),
            SourceLocation(4, 46)
        )
        assertThat(errors[0]).hasMessage(ComposeModifierWithoutDefault.MissingModifierDefaultParam)
        assertThat(errors[1]).hasMessage(ComposeModifierWithoutDefault.MissingModifierDefaultParam)
    }

    @Test
    fun `passes when a Composable inside of an interface has modifiers but without default values`() {
        @Language("kotlin")
        val composableCode = """
                interface Bleh {
                    @Composable
                    fun Something(modifier: Modifier)
                }
        """.trimIndent()

        val errors = rule.lint(composableCode)
        assertThat(errors).isEmpty()
    }

    @Test
    fun `passes when a Composable has modifiers with defaults`() {
        @Language("kotlin")
        val code =
            """
                @Composable
                fun Something(modifier: Modifier = Modifier) {
                    Row(modifier = modifier) {
                    }
                }
                @Composable
                fun Something(modifier: Modifier = Modifier.fillMaxSize()) {
                    Row(modifier = modifier) {
                    }
                }
                @Composable
                fun Something(modifier: Modifier = SomeOtherValueFromSomeConstant) {
                    Row(modifier = modifier) {
                    }
                }
            """.trimIndent()
        val errors = rule.lint(code)
        assertThat(errors).isEmpty()
    }
}
