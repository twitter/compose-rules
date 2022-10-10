// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules.ktlint

import com.twitter.compose.rules.ComposeUnstableCollections
import com.twitter.rules.core.ComposeKtVisitor
import com.twitter.rules.core.ktlint.TwitterKtlintRule

class ComposeUnstableCollectionsCheck :
    TwitterKtlintRule("twitter-compose:unstable-collections"),
    ComposeKtVisitor by ComposeUnstableCollections()
