// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules.ktlint

import com.pinterest.ktlint.test.KtLintAssertThat.Companion.assertThatRule
import com.pinterest.ktlint.test.LintViolation
import com.twitter.compose.rules.ComposeCompositionLocalAllowlist
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

class ComposeCompositionLocalAllowlistCheckTest {

    private val allowlistRuleAssertThat = assertThatRule { ComposeCompositionLocalAllowlistCheck() }

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
        allowlistRuleAssertThat(code)
            .hasLintViolationsWithoutAutoCorrect(
                LintViolation(
                    line = 1,
                    col = 13,
                    detail = ComposeCompositionLocalAllowlist.CompositionLocalNotInAllowlist,
                ),
                LintViolation(
                    line = 2,
                    col = 14,
                    detail = ComposeCompositionLocalAllowlist.CompositionLocalNotInAllowlist,
                ),
                LintViolation(
                    line = 3,
                    col = 5,
                    detail = ComposeCompositionLocalAllowlist.CompositionLocalNotInAllowlist,
                ),
                LintViolation(
                    line = 4,
                    col = 13,
                    detail = ComposeCompositionLocalAllowlist.CompositionLocalNotInAllowlist,
                ),
            )
    }

    @Test
    fun `passes when a CompositionLocal is defined but it's in the allowlist`() {
        @Language("kotlin")
        val code =
            """
                val LocalBanana = staticCompositionLocalOf<String> { "Banana" }
                val LocalPotato = compositionLocalOf { "Potato" }
            """.trimIndent()
        allowlistRuleAssertThat(code)
            .withEditorConfigOverride(
                compositionLocalAllowlistProperty to "LocalPotato,LocalBanana",
            )
            .hasNoLintViolations()
    }
}
