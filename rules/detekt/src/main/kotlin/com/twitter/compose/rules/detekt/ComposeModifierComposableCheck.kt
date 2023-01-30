// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules.detekt

import com.twitter.compose.rules.ComposeModifierComposable
import com.twitter.rules.core.ComposeKtVisitor
import com.twitter.rules.core.detekt.TwitterDetektRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Severity

class ComposeModifierComposableCheck(config: Config) :
    TwitterDetektRule(config),
    ComposeKtVisitor by ComposeModifierComposable() {
    override val issue: Issue = Issue(
        id = "ModifierComposable",
        severity = Severity.Performance,
        description = ComposeModifierComposable.ComposableModifier,
        debt = Debt.TEN_MINS,
    )
}
