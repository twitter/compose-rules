// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules.detekt

import com.twitter.compose.rules.ComposeModifierMissing
import com.twitter.rules.core.ComposeKtVisitor
import com.twitter.rules.core.detekt.TwitterDetektRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Severity

class ComposeModifierMissingCheck(config: Config) :
    TwitterDetektRule(config),
    ComposeKtVisitor by ComposeModifierMissing() {
    override val issue: Issue = Issue(
        id = "ModifierMissing",
        severity = Severity.Defect,
        description = ComposeModifierMissing.MissingModifierContentComposable,
        debt = Debt.TEN_MINS,
    )
}
