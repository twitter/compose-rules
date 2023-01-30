// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules.detekt

import com.twitter.compose.rules.ComposeUnstableCollections.Companion.createErrorMessage
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

class ComposeUnstableCollectionsCheckTest {

    private val rule = ComposeUnstableCollectionsCheck(Config.empty)

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
        val errors = rule.lint(code)
        assertThat(errors)
            .hasStartSourceLocations(
                SourceLocation(2, 18),
                SourceLocation(4, 18),
                SourceLocation(6, 18),
            )
        assertThat(errors[0]).hasMessage(createErrorMessage("List<String>", "List", "a"))
        assertThat(errors[1]).hasMessage(createErrorMessage("Set<String>", "Set", "a"))
        assertThat(errors[2]).hasMessage(createErrorMessage("Map<String, Int>", "Map", "a"))
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
        val errors = rule.lint(code)
        assertThat(errors).isEmpty()
    }
}
