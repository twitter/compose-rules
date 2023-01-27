// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules.detekt

import com.twitter.compose.rules.ComposePreviewNaming
import com.twitter.rules.core.ComposeKtVisitor
import com.twitter.rules.core.detekt.TwitterDetektRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Severity

class ComposePreviewNamingCheck(config: Config) :
    TwitterDetektRule(config),
    ComposeKtVisitor by ComposePreviewNaming() {

    override val issue: Issue = Issue(
        id = "PreviewNaming",
        severity = Severity.CodeSmell,
        description = "Multipreview annotations should end with the `Previews` suffix",
        debt = Debt.FIVE_MINS,
    )
}
