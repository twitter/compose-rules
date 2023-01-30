// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules.detekt

import com.twitter.compose.rules.ComposeParameterOrder
import com.twitter.rules.core.ComposeKtVisitor
import com.twitter.rules.core.detekt.TwitterDetektRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Severity

class ComposeParameterOrderCheck(config: Config) :
    TwitterDetektRule(config),
    ComposeKtVisitor by ComposeParameterOrder() {
    override val issue: Issue = Issue(
        id = "ComposableParamOrder",
        severity = Severity.CodeSmell,
        description = "Parameters in a composable function should be ordered following this pattern: " +
            "params without defaults, modifiers, params with defaults and optionally, " +
            "a trailing function that might not have a default param.",
        debt = Debt.TEN_MINS,
    )
}
