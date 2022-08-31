// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.rules.ktlint.compose

import com.twitter.compose.rules.ComposeModifierWithoutDefault
import com.twitter.rules.core.ComposeKtVisitor
import com.twitter.rules.core.ktlint.TwitterKtlintRule

class ComposeModifierWithoutDefaultCheck :
    TwitterKtlintRule("twitter-compose:modifier-without-default-check"),
    ComposeKtVisitor by ComposeModifierWithoutDefault()
