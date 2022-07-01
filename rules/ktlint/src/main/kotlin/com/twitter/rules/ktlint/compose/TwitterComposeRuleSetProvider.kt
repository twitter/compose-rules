package com.twitter.rules.ktlint.compose

import com.pinterest.ktlint.core.RuleSet
import com.pinterest.ktlint.core.RuleSetProvider

class TwitterComposeRuleSetProvider : RuleSetProvider {
    override fun get(): RuleSet = RuleSet(
        "twitter-compose",
        ComposeModifierComposableCheck(),
        ComposeModifierMissingCheck(),
        ComposeModifierReusedCheck(),
        ComposeModifierWithoutDefaultCheck(),
        ComposeMultipleContentEmittersCheck(),
        ComposeMutableParametersCheck(),
        ComposeNamingCheck(),
        ComposeParameterOrderCheck(),
        ComposeRememberMissingCheck(),
        ComposeViewModelForwardingCheck()
    )
}
