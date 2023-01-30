// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules.detekt

import com.twitter.compose.rules.ComposeViewModelInjection
import com.twitter.rules.core.ComposeKtVisitor
import com.twitter.rules.core.detekt.TwitterDetektRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Severity

class ComposeViewModelInjectionCheck(config: Config) :
    TwitterDetektRule(config),
    ComposeKtVisitor by ComposeViewModelInjection() {

    override val issue: Issue = Issue(
        id = "ViewModelInjection",
        severity = Severity.CodeSmell,
        description = """
            Implicit dependencies of composables should be made explicit.

            Acquiring a ViewModel should be done in composable default parameters, so that it is more testable and flexible.
        """.trimIndent(),
        debt = Debt.TEN_MINS,
    )
}
