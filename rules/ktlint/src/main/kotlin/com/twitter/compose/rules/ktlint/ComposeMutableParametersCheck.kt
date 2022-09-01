// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules.ktlint

import com.twitter.compose.rules.ComposeMutableParameters
import com.twitter.rules.core.ComposeKtVisitor
import com.twitter.rules.core.ktlint.TwitterKtlintRule

class ComposeMutableParametersCheck :
    TwitterKtlintRule("twitter-compose:mutable-params-check"),
    ComposeKtVisitor by ComposeMutableParameters()
