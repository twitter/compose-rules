// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules.detekt

import com.twitter.compose.rules.ComposeNaming
import com.twitter.compose.rules.ComposeNaming.Type.CheckDontReturnResults
import com.twitter.compose.rules.ComposeNaming.Type.CheckReturnResults
import com.twitter.rules.core.ComposeKtVisitor
import com.twitter.rules.core.detekt.TwitterDetektRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Severity

class ComposeNamingReturnResultsCheck(config: Config) :
    TwitterDetektRule(config),
    ComposeKtVisitor by ComposeNaming(CheckReturnResults) {
    override val issue: Issue = Issue(
        id = "naming-check",
        severity = Severity.CodeSmell,
        description = ComposeNaming.ComposablesThatReturnResultsShouldBeLowercase,
        debt = Debt.TEN_MINS
    )
}

class ComposeNamingDontReturnResultsCheck(config: Config) :
    TwitterDetektRule(config),
    ComposeKtVisitor by ComposeNaming(CheckDontReturnResults) {
    override val issue: Issue = Issue(
        id = "naming-check",
        severity = Severity.CodeSmell,
        description = ComposeNaming.ComposablesThatDoNotReturnResultsShouldBeCapitalized,
        debt = Debt.TEN_MINS
    )
}
