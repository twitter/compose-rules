// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules.detekt

import com.twitter.compose.rules.ComposeUnstableCollections
import com.twitter.rules.core.ComposeKtVisitor
import com.twitter.rules.core.detekt.TwitterDetektRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Severity

class ComposeUnstableCollectionsCheck(config: Config) :
    TwitterDetektRule(config),
    ComposeKtVisitor by ComposeUnstableCollections() {
    override val issue: Issue = Issue(
        id = "UnstableCollections",
        severity = Severity.Defect,
        description = """
            The Compose Compiler cannot infer the stability of a parameter if a List/Set/Map is used in it, even if the item type is stable.
            You should use Kotlinx Immutable Collections instead, or create an `@Immutable` wrapper for this class.

            See https://twitter.github.io/compose-rules/rules/#avoid-using-unstable-collections for more information.
        """.trimIndent(),
        debt = Debt.TWENTY_MINS,
    )
}
