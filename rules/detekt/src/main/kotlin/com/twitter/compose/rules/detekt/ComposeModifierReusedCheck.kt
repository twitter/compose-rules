// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules.detekt

import com.twitter.compose.rules.ComposeModifierReused
import com.twitter.rules.core.ComposeKtVisitor
import com.twitter.rules.core.detekt.TwitterDetektRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Severity

class ComposeModifierReusedCheck(config: Config) :
    TwitterDetektRule(config),
    ComposeKtVisitor by ComposeModifierReused() {
    override val issue: Issue = Issue(
        id = "ModifierReused",
        severity = Severity.Defect,
        description = ComposeModifierReused.ModifierShouldBeUsedOnceOnly,
        debt = Debt.TWENTY_MINS,
    )
}
