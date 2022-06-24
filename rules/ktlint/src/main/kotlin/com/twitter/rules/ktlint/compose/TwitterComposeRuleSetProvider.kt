package com.twitter.rules.ktlint.compose

import com.pinterest.ktlint.core.RuleSet
import com.pinterest.ktlint.core.RuleSetProvider

class TwitterComposeRuleSetProvider : RuleSetProvider {
    override fun get(): RuleSet = RuleSet(
        "twitter-compose",
        ComposeModifierMissingCheck(),
        ComposeModifierUsedOnceCheck(),
        ComposeMultipleContentEmittersCheck(),
        ComposeMutableParametersCheck(),
        ComposeNamingCheck(),
        ComposeParameterOrderCheck(),
        ComposeRememberMissingCheck(),
        ComposeViewModelForwardingCheck(),
    )
}
