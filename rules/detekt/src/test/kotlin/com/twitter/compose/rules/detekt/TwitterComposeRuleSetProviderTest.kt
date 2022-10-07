// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules.detekt

import com.twitter.rules.core.detekt.TwitterDetektRule
import io.gitlab.arturbosch.detekt.api.Config
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test
import org.reflections.Reflections

class TwitterComposeRuleSetProviderTest {

    private val ruleSetProvider = TwitterComposeRuleSetProvider()
    private val ruleSet = ruleSetProvider.instance(Config.empty)

    @Test
    fun `ensure all rules in the package are represented in the ruleset`() {
        val reflections = Reflections(ruleSetProvider.javaClass.packageName)
        val ruleClassesInPackage = reflections.getSubTypesOf(TwitterDetektRule::class.java)
        val ruleClassesInRuleSet = ruleSet.rules.filterIsInstance<TwitterDetektRule>().map { it::class.java }.toSet()
        assertThat(ruleClassesInRuleSet).containsExactlyInAnyOrderElementsOf(ruleClassesInPackage)
    }
}
