// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules.ktlint

import com.twitter.compose.rules.ComposeModifierMissing
import com.twitter.rules.core.ComposeKtVisitor
import com.twitter.rules.core.ktlint.TwitterKtlintRule

class ComposeModifierMissingCheck :
    TwitterKtlintRule("twitter-compose:modifier-missing-check"),
    ComposeKtVisitor by ComposeModifierMissing()
