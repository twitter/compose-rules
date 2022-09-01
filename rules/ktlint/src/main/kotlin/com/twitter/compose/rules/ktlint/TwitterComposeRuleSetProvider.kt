// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
@file:Suppress("DEPRECATION")

package com.twitter.compose.rules.ktlint

import com.pinterest.ktlint.core.RuleProvider
import com.pinterest.ktlint.core.RuleSet
import com.pinterest.ktlint.core.RuleSetProvider
import com.pinterest.ktlint.core.RuleSetProviderV2

class TwitterComposeRuleSetProvider :
    RuleSetProviderV2(
        com.twitter.compose.rules.ktlint.TwitterComposeRuleSetProvider.Companion.CustomRuleSetId,
        com.twitter.compose.rules.ktlint.TwitterComposeRuleSetProvider.Companion.RuleSetAbout
    ),
    RuleSetProvider {

    // Pre-0.47.0 ruleset (will go away in 0.48.0)
    @Suppress("OVERRIDE_DEPRECATION")
    override fun get(): RuleSet = RuleSet(
        com.twitter.compose.rules.ktlint.TwitterComposeRuleSetProvider.Companion.CustomRuleSetId,
        com.twitter.compose.rules.ktlint.ComposeModifierComposableCheck(),
        com.twitter.compose.rules.ktlint.ComposeModifierMissingCheck(),
        com.twitter.compose.rules.ktlint.ComposeModifierReusedCheck(),
        com.twitter.compose.rules.ktlint.ComposeModifierWithoutDefaultCheck(),
        com.twitter.compose.rules.ktlint.ComposeMultipleContentEmittersCheck(),
        com.twitter.compose.rules.ktlint.ComposeMutableParametersCheck(),
        com.twitter.compose.rules.ktlint.ComposeNamingCheck(),
        com.twitter.compose.rules.ktlint.ComposeParameterOrderCheck(),
        com.twitter.compose.rules.ktlint.ComposeRememberMissingCheck(),
        com.twitter.compose.rules.ktlint.ComposeViewModelForwardingCheck()
    )

    // 0.47.0+ ruleset
    override fun getRuleProviders(): Set<RuleProvider> = setOf(
        RuleProvider { com.twitter.compose.rules.ktlint.ComposeModifierComposableCheck() },
        RuleProvider { com.twitter.compose.rules.ktlint.ComposeModifierMissingCheck() },
        RuleProvider { com.twitter.compose.rules.ktlint.ComposeModifierReusedCheck() },
        RuleProvider { com.twitter.compose.rules.ktlint.ComposeModifierWithoutDefaultCheck() },
        RuleProvider { com.twitter.compose.rules.ktlint.ComposeMultipleContentEmittersCheck() },
        RuleProvider { com.twitter.compose.rules.ktlint.ComposeMutableParametersCheck() },
        RuleProvider { com.twitter.compose.rules.ktlint.ComposeNamingCheck() },
        RuleProvider { com.twitter.compose.rules.ktlint.ComposeParameterOrderCheck() },
        RuleProvider { com.twitter.compose.rules.ktlint.ComposeRememberMissingCheck() },
        RuleProvider { com.twitter.compose.rules.ktlint.ComposeViewModelForwardingCheck() }
    )

    private companion object {
        private val RuleSetAbout = About(
            maintainer = "Twitter, Inc",
            description = "Static checks to aid with a healthy adoption of Jetpack Compose",
            license = "Apache License, Version 2.0",
            repositoryUrl = "https://github.com/twitter/compose-rules/",
            issueTrackerUrl = "https://github.com/twitter/compose-rules/issues"
        )
        const val CustomRuleSetId = "twitter-compose"
    }
}
