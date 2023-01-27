// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules.detekt

import com.twitter.compose.rules.ComposePreviewPublic
import com.twitter.rules.core.ComposeKtVisitor
import com.twitter.rules.core.detekt.TwitterDetektRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Severity

class ComposePreviewPublicCheck(config: Config) :
    TwitterDetektRule(config),
    ComposeKtVisitor by ComposePreviewPublic() {

    override val issue: Issue = Issue(
        id = "PreviewPublic",
        severity = Severity.CodeSmell,
        description = ComposePreviewPublic.ComposablesPreviewShouldNotBePublic,
        debt = Debt.FIVE_MINS,
    )
}
