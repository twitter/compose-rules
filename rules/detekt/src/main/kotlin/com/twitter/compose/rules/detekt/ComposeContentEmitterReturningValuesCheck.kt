// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules.detekt

import com.twitter.compose.rules.ComposeMultipleContentEmitters
import com.twitter.compose.rules.ComposeMultipleContentEmitters.Detector.ContentEmitterReturningValues
import com.twitter.rules.core.ComposeKtVisitor
import com.twitter.rules.core.detekt.TwitterDetektRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Severity

class ComposeContentEmitterReturningValuesCheck(config: Config) :
    TwitterDetektRule(config),
    ComposeKtVisitor by ComposeMultipleContentEmitters(ContentEmitterReturningValues) {
    override val issue: Issue = Issue(
        id = "content-emitter-returning-values-check",
        severity = Severity.Defect,
        description = ComposeMultipleContentEmitters.ContentEmitterReturningValuesToo,
        debt = Debt.TWENTY_MINS
    )
}
