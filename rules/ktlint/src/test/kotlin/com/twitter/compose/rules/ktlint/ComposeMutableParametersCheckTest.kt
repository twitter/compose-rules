// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules.ktlint

import com.pinterest.ktlint.test.KtLintAssertThat.Companion.assertThatRule
import com.pinterest.ktlint.test.LintViolation
import com.twitter.compose.rules.ComposeMutableParameters
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

class ComposeMutableParametersCheckTest {

    private val mutableParamRuleAssertThat = assertThatRule { ComposeMutableParametersCheck() }

    @Test
    fun `errors when a Composable has a mutable parameter`() {
        @Language("kotlin")
        val code =
            """
                @Composable
                fun Something(a: MutableState<String>) {}
                @Composable
                fun Something(a: ArrayList<String>) {}
                @Composable
                fun Something(a: HashSet<String>) {}
                @Composable
                fun Something(a: MutableMap<String, String>) {}
            """.trimIndent()
        mutableParamRuleAssertThat(code).hasLintViolationsWithoutAutoCorrect(
            LintViolation(
                line = 2,
                col = 15,
                detail = ComposeMutableParameters.MutableParameterInCompose,
            ),
            LintViolation(
                line = 4,
                col = 15,
                detail = ComposeMutableParameters.MutableParameterInCompose,
            ),
            LintViolation(
                line = 6,
                col = 15,
                detail = ComposeMutableParameters.MutableParameterInCompose,
            ),
            LintViolation(
                line = 8,
                col = 15,
                detail = ComposeMutableParameters.MutableParameterInCompose,
            ),
        )
    }

    @Test
    fun `no errors when a Composable has valid parameters`() {
        @Language("kotlin")
        val code =
            """
                @Composable
                fun Something(a: String, b: (Int) -> Unit) {}
                @Composable
                fun Something(a: State<String>) {}
            """.trimIndent()
        mutableParamRuleAssertThat(code).hasNoLintViolations()
    }
}
