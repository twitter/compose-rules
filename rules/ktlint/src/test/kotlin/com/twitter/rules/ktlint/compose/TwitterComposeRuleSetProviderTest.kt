// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.rules.ktlint.compose

import com.pinterest.ktlint.test.RuleSetProviderTest

class TwitterComposeRuleSetProviderTest : RuleSetProviderTest(
    rulesetClass = TwitterComposeRuleSetProvider::class.java,
    packageName = "com.twitter.rules.ktlint.compose"
)
