// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules.detekt

import com.twitter.compose.rules.ComposeMultipleContentEmitters
import com.twitter.rules.core.ComposeKtVisitor
import com.twitter.rules.core.detekt.TwitterDetektRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Severity

class ComposeMultipleContentEmittersCheck(config: Config) :
    TwitterDetektRule(config),
    ComposeKtVisitor by ComposeMultipleContentEmitters() {

    override val issue: Issue = Issue(
        id = "MultipleEmitters",
        severity = Severity.Defect,
        description = ComposeMultipleContentEmitters.MultipleContentEmittersDetected,
        debt = Debt.TWENTY_MINS,
    )
}
