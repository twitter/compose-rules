// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules.detekt

import com.twitter.compose.rules.ComposeContentEmitterReturningValues
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

class ComposeContentEmitterReturningValuesCheckTest {

    private val rule = ComposeContentEmitterReturningValuesCheck(Config.empty)

    @Test
    fun `error out when detecting a content emitting composable that returns something other than unit`() {
        @Language("kotlin")
        val code =
            """
                @Composable
                fun Something(): String { // This one emits content directly and should fail
                    Text("Hi")
                    return "Potato"
                }
                @Composable
                fun Something2(): WhateverState { // This one emits content indirectly and should fail too
                    Something3()
                    return remember { WhateverState() }
                }
                @Composable
                fun Something3() { // This one is fine but calling it should make Something2 fail
                    HorizonIcon(icon = HorizonIcon.Arrow)
                }
            """.trimIndent()
        val errors = rule.lint(code)
        assertThat(errors).hasSize(2)
            .hasSourceLocations(
                SourceLocation(2, 5),
                SourceLocation(7, 5)
            )
        for (error in errors) {
            assertThat(error).hasMessage(ComposeContentEmitterReturningValues.ContentEmitterReturningValuesToo)
        }
    }
}
