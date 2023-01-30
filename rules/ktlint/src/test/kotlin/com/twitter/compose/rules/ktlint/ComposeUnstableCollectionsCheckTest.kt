// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules.ktlint

import com.pinterest.ktlint.test.KtLintAssertThat.Companion.assertThatRule
import com.pinterest.ktlint.test.LintViolation
import com.twitter.compose.rules.ComposeUnstableCollections.Companion.createErrorMessage
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

class ComposeUnstableCollectionsCheckTest {

    private val ruleAssertThat = assertThatRule { ComposeUnstableCollectionsCheck() }

    @Test
    fun `errors when a Composable has a List Set Map parameter`() {
        @Language("kotlin")
        val code =
            """
                @Composable
                fun Something(a: List<String>) {}
                @Composable
                fun Something(a: Set<String>) {}
                @Composable
                fun Something(a: Map<String, Int>) {}
            """.trimIndent()
        ruleAssertThat(code).hasLintViolationsWithoutAutoCorrect(
            LintViolation(
                line = 2,
                col = 18,
                detail = createErrorMessage("List<String>", "List", "a"),
            ),
            LintViolation(
                line = 4,
                col = 18,
                detail = createErrorMessage("Set<String>", "Set", "a"),
            ),
            LintViolation(
                line = 6,
                col = 18,
                detail = createErrorMessage("Map<String, Int>", "Map", "a"),
            ),
        )
    }

    @Test
    fun `no errors when a Composable has valid parameters`() {
        @Language("kotlin")
        val code =
            """
                @Composable
                fun Something(a: ImmutableList<String>, b: ImmutableSet<String>, c: ImmutableMap<String, Int>) {}
                @Composable
                fun Something(a: StringList, b: StringSet, c: StringToIntMap) {}
            """.trimIndent()
        ruleAssertThat(code).hasNoLintViolations()
    }
}
