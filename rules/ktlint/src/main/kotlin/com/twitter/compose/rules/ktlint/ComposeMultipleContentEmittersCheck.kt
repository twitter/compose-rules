// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules.ktlint

import com.twitter.compose.rules.ComposeMultipleContentEmitters
import com.twitter.compose.rules.ComposeMultipleContentEmitters.Detector.MultipleContentEmitters
import com.twitter.rules.core.ComposeKtVisitor
import com.twitter.rules.core.ktlint.TwitterKtlintRule

class ComposeMultipleContentEmittersCheck :
    TwitterKtlintRule("twitter-compose:multiple-emitters-check"),
    ComposeKtVisitor by ComposeMultipleContentEmitters(MultipleContentEmitters)
