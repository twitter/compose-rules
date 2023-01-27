// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules.ktlint

import com.pinterest.ktlint.test.KtLintAssertThat.Companion.assertThatRule
import com.pinterest.ktlint.test.LintViolation
import com.twitter.compose.rules.ComposeCompositionLocalNaming
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

class ComposeCompositionLocalNamingCheckTest {

    private val ruleAssertThat = assertThatRule { ComposeCompositionLocalNamingCheck() }

    @Test
    fun `error when a CompositionLocal has a wrong name`() {
        @Language("kotlin")
        val code =
            """
                val AppleLocal = staticCompositionLocalOf<String> { "Apple" }
                val Plum: String = staticCompositionLocalOf { "Plum" }
            """.trimIndent()
        ruleAssertThat(code)
            .hasLintViolationsWithoutAutoCorrect(
                LintViolation(
                    line = 1,
                    col = 5,
                    detail = ComposeCompositionLocalNaming.CompositionLocalNeedsLocalPrefix,
                ),
                LintViolation(
                    line = 2,
                    col = 5,
                    detail = ComposeCompositionLocalNaming.CompositionLocalNeedsLocalPrefix,
                ),
            )
    }

    @Test
    fun `passes when a CompositionLocal is well named`() {
        @Language("kotlin")
        val code =
            """
                val LocalBanana = staticCompositionLocalOf<String> { "Banana" }
                val LocalPotato = compositionLocalOf { "Potato" }
            """.trimIndent()
        ruleAssertThat(code).hasNoLintViolations()
    }
}
