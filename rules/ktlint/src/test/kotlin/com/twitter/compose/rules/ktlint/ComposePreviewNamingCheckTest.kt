// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules.ktlint

import com.pinterest.ktlint.test.KtLintAssertThat.Companion.assertThatRule
import com.pinterest.ktlint.test.LintViolation
import com.twitter.compose.rules.ComposePreviewNaming
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

class ComposePreviewNamingCheckTest {

    private val ruleAssertThat = assertThatRule { ComposePreviewNamingCheck() }

    @Test
    fun `passes for non-preview annotations`() {
        @Language("kotlin")
        val code =
            """
            annotation class Banana
            """.trimIndent()
        ruleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `passes for preview annotations with the proper names`() {
        @Language("kotlin")
        val code =
            """
            @Preview
            annotation class BananaPreview
            @BananaPreview
            annotation class DoubleBananaPreview
            @Preview
            @Preview
            annotation class ApplePreviews
            @Preview
            @ApplePreviews
            annotation class CombinedApplePreviews
            @BananaPreview
            @ApplePreviews
            annotation class FruitBasketPreviews
            """.trimIndent()
        ruleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `errors when a multipreview annotation is not correctly named for 1 preview`() {
        @Language("kotlin")
        val code =
            """
            @Preview
            annotation class Banana
            @Preview
            annotation class BananaPreviews
            @BananaPreview
            annotation class WithBananaPreviews
            """.trimIndent()
        ruleAssertThat(code).hasLintViolationsWithoutAutoCorrect(
            LintViolation(
                line = 2,
                col = 18,
                detail = ComposePreviewNaming.createMessage(1, "Preview"),
            ),
            LintViolation(
                line = 4,
                col = 18,
                detail = ComposePreviewNaming.createMessage(1, "Preview"),
            ),
            LintViolation(
                line = 6,
                col = 18,
                detail = ComposePreviewNaming.createMessage(1, "Preview"),
            ),
        )
    }

    @Test
    fun `errors when a multipreview annotation is not correctly named for multi previews`() {
        @Language("kotlin")
        val code =
            """
            @Preview
            @Preview
            annotation class BananaPreview
            @BananaPreview
            @BananaPreview
            annotation class BananaPreview
            """.trimIndent()
        ruleAssertThat(code).hasLintViolationsWithoutAutoCorrect(
            LintViolation(
                line = 3,
                col = 18,
                detail = ComposePreviewNaming.createMessage(2, "Previews"),
            ),
            LintViolation(
                line = 6,
                col = 18,
                detail = ComposePreviewNaming.createMessage(2, "Previews"),
            ),
        )
    }
}
