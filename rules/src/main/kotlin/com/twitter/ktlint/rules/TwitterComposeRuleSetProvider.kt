package com.twitter.ktlint.rules

import com.pinterest.ktlint.core.RuleSet
import com.pinterest.ktlint.core.RuleSetProvider

class TwitterRuleSetProvider : RuleSetProvider {
    override fun get(): RuleSet = RuleSet(
        "twitter-compose",
    )
}
