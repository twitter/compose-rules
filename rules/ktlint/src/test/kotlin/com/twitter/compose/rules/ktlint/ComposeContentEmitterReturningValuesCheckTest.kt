// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules.ktlint

import com.pinterest.ktlint.test.KtLintAssertThat.Companion.assertThatRule
import com.pinterest.ktlint.test.LintViolation
import com.twitter.compose.rules.ComposeContentEmitterReturningValues
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

class ComposeContentEmitterReturningValuesCheckTest {

    private val emittersRuleAssertThat = assertThatRule { ComposeContentEmitterReturningValuesCheck() }

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
        emittersRuleAssertThat(code)
            .withEditorConfigOverride(
                contentEmittersProperty to "Potato,Banana",
            )
            .hasLintViolationsWithoutAutoCorrect(
                LintViolation(
                    line = 2,
                    col = 5,
                    detail = ComposeContentEmitterReturningValues.ContentEmitterReturningValuesToo,
                ),
                LintViolation(
                    line = 7,
                    col = 5,
                    detail = ComposeContentEmitterReturningValues.ContentEmitterReturningValuesToo,
                ),
                LintViolation(
                    line = 16,
                    col = 5,
                    detail = ComposeContentEmitterReturningValues.ContentEmitterReturningValuesToo,
                ),
            )
    }
}
