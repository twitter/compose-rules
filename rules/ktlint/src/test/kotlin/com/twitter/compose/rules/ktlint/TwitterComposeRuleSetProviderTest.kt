// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules.ktlint

import com.twitter.rules.core.ktlint.TwitterKtlintRule
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test
import org.reflections.Reflections

class TwitterComposeRuleSetProviderTest {

    private val ruleSetProvider = TwitterComposeRuleSetProvider()
    private val ruleClassesInPackage = Reflections(ruleSetProvider.javaClass.packageName)
        .getSubTypesOf(TwitterKtlintRule::class.java)

    @Test
    fun `ensure all rules in the package are represented in the ruleset`() {
        val ruleSet = ruleSetProvider.getRuleProviders()
        val ruleClassesInRuleSet = ruleSet.map { it.createNewRuleInstance() }
            .filterIsInstance<TwitterKtlintRule>()
            .map { it::class.java }
            .toSet()
        assertThat(ruleClassesInRuleSet).containsExactlyInAnyOrderElementsOf(ruleClassesInPackage)
    }
}
