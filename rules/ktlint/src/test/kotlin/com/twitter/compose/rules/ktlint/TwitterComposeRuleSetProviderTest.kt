// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules.ktlint

import com.pinterest.ktlint.test.RuleSetProviderTest

class TwitterComposeRuleSetProviderTest : RuleSetProviderTest(
    rulesetClass = TwitterComposeRuleSetProvider::class.java,
    packageName = "com.twitter.rules.ktlint.compose"
)
