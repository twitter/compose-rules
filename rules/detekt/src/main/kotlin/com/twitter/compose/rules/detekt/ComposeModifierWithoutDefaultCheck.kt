// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules.detekt

import com.twitter.compose.rules.ComposeModifierWithoutDefault
import com.twitter.rules.core.ComposeKtVisitor
import com.twitter.rules.core.detekt.TwitterDetektRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Severity

class ComposeModifierWithoutDefaultCheck(config: Config) :
    TwitterDetektRule(config),
    ComposeKtVisitor by ComposeModifierWithoutDefault() {

    override val issue: Issue = Issue(
        id = "ModifierWithoutDefault",
        severity = Severity.CodeSmell,
        description = ComposeModifierWithoutDefault.MissingModifierDefaultParam,
        debt = Debt.FIVE_MINS,
    )
}
