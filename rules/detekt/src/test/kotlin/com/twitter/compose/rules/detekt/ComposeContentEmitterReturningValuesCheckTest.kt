// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules.detekt

import com.twitter.compose.rules.ComposeContentEmitterReturningValues
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

class ComposeContentEmitterReturningValuesCheckTest {

    private val testConfig = TestConfig(
        "contentEmitters" to listOf("Potato", "Banana"),
    )
    private val rule = ComposeContentEmitterReturningValuesCheck(testConfig)

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
                    Potato(icon = HorizonIcon.Arrow)
                }
                @Composable
                fun Something4(): String { // This one is using a composable defined in the config
                    Banana()
                }
            """.trimIndent()
        val errors = rule.lint(code)
        assertThat(errors)
            .hasStartSourceLocations(
                SourceLocation(2, 5),
                SourceLocation(7, 5),
                SourceLocation(16, 5),
            )
        for (error in errors) {
            assertThat(error).hasMessage(ComposeContentEmitterReturningValues.ContentEmitterReturningValuesToo)
        }
    }
}
