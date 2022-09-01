// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules.detekt

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

class TwitterComposeRuleSetProvider : RuleSetProvider {
    override val ruleSetId: String = CustomRuleSetId

    override fun instance(config: Config): RuleSet = RuleSet(
        CustomRuleSetId,
        listOf(
            ComposeModifierComposableCheck(config),
            ComposeModifierMissingCheck(config),
            /* TODO ComposeModifierReusedCheck(), */
            /* TODO ComposeModifierWithoutDefaultCheck(), */
            /* TODO ComposeMultipleContentEmittersCheck(), */
            /* TODO ComposeMutableParametersCheck(), */
            /* TODO ComposeNamingCheck(), */
            /* TODO ComposeParameterOrderCheck(), */
            /* TODO ComposeRememberMissingCheck(), */
            /* TODO ComposeViewModelForwardingCheck() */
        )
    )

    private companion object {
        const val CustomRuleSetId = "twitter-compose"
    }
}
