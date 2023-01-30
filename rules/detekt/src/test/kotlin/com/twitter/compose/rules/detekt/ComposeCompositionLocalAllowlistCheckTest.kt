// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules.detekt

import com.twitter.compose.rules.ComposeCompositionLocalAllowlist
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

class ComposeCompositionLocalAllowlistCheckTest {

    private val testConfig = TestConfig(
        "allowedCompositionLocals" to listOf("LocalBanana", "LocalPotato"),
    )
    private val rule = ComposeCompositionLocalAllowlistCheck(testConfig)

    @Test
    fun `error when a CompositionLocal is defined`() {
        @Language("kotlin")
        val code =
            """
                private val LocalApple = staticCompositionLocalOf<String> { "Apple" }
                internal val LocalPlum: String = staticCompositionLocalOf { "Plum" }
                val LocalPrune = compositionLocalOf { "Prune" }
                private val LocalKiwi: String = compositionLocalOf { "Kiwi" }
            """.trimIndent()
        val errors = rule.lint(code)
        assertThat(errors)
            .hasStartSourceLocations(
                SourceLocation(1, 13),
                SourceLocation(2, 14),
                SourceLocation(3, 5),
                SourceLocation(4, 13),
            )
        for (error in errors) {
            assertThat(error).hasMessage(ComposeCompositionLocalAllowlist.CompositionLocalNotInAllowlist)
        }
    }

    @Test
    fun `passes when a CompositionLocal is defined but it's in the allowlist`() {
        @Language("kotlin")
        val code =
            """
                val LocalBanana = staticCompositionLocalOf<String> { "Banana" }
                val LocalPotato = compositionLocalOf { "Potato" }
            """.trimIndent()
        val errors = rule.lint(code)
        assertThat(errors).isEmpty()
    }
}
