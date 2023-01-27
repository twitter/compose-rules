// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules.detekt

import com.twitter.compose.rules.ComposeNaming
import com.twitter.rules.core.ComposeKtVisitor
import com.twitter.rules.core.detekt.TwitterDetektRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Severity

class ComposeNamingCheck(config: Config) :
    TwitterDetektRule(config),
    ComposeKtVisitor by ComposeNaming() {
    override val issue: Issue = Issue(
        id = "ComposableNaming",
        severity = Severity.CodeSmell,
        description = """
        Composable functions that return Unit should start with an uppercase letter. They are considered declarative entities that can be either present or absent in a composition and therefore follow the naming rules for classes.

        However, Composable functions that return a value should start with a lowercase letter instead. They should follow the standard Kotlin Coding Conventions for the naming of functions for any function annotated @Composable that returns a value other than Unit
        """.trimIndent(),
        debt = Debt.TEN_MINS,
    )
}
