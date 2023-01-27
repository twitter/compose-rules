// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules.detekt

import com.twitter.compose.rules.ComposeRememberMissing
import com.twitter.rules.core.ComposeKtVisitor
import com.twitter.rules.core.detekt.TwitterDetektRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Severity

class ComposeRememberMissingCheck(config: Config) :
    TwitterDetektRule(config),
    ComposeKtVisitor by ComposeRememberMissing() {

    override val issue: Issue = Issue(
        id = "RememberMissing",
        severity = Severity.Defect,
        description = """
            Using mutableStateOf/derivedStateOf in a @Composable function without it being inside of a remember function.
            If you don't remember the state instance, a new state instance will be created when the function is recomposed.
        """.trimIndent(),
        debt = Debt.FIVE_MINS,
    )
}
