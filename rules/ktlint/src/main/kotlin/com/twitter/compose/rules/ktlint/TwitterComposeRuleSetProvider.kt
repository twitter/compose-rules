// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
@file:Suppress("DEPRECATION")

package com.twitter.compose.rules.ktlint

import com.pinterest.ktlint.core.RuleProvider
import com.pinterest.ktlint.core.RuleSetProviderV2

class TwitterComposeRuleSetProvider : RuleSetProviderV2(
    CustomRuleSetId,
    RuleSetAbout,
) {

    override fun getRuleProviders(): Set<RuleProvider> = setOf(
        RuleProvider { ComposeCompositionLocalAllowlistCheck() },
        RuleProvider { ComposeCompositionLocalNamingCheck() },
        RuleProvider { ComposeContentEmitterReturningValuesCheck() },
        RuleProvider { ComposeModifierComposableCheck() },
        RuleProvider { ComposeModifierMissingCheck() },
        RuleProvider { ComposeModifierReusedCheck() },
        RuleProvider { ComposeModifierWithoutDefaultCheck() },
        RuleProvider { ComposeMultipleContentEmittersCheck() },
        RuleProvider { ComposeMutableParametersCheck() },
        RuleProvider { ComposeNamingCheck() },
        RuleProvider { ComposeParameterOrderCheck() },
        RuleProvider { ComposePreviewNamingCheck() },
        RuleProvider { ComposePreviewPublicCheck() },
        RuleProvider { ComposeRememberMissingCheck() },
        RuleProvider { ComposeUnstableCollectionsCheck() },
        RuleProvider { ComposeViewModelForwardingCheck() },
        RuleProvider { ComposeViewModelInjectionCheck() },
    )

    private companion object {
        private val RuleSetAbout = About(
            maintainer = "Twitter, Inc",
            description = "Static checks to aid with a healthy adoption of Jetpack Compose",
            license = "Apache License, Version 2.0",
            repositoryUrl = "https://github.com/twitter/compose-rules/",
            issueTrackerUrl = "https://github.com/twitter/compose-rules/issues",
        )
        const val CustomRuleSetId = "twitter-compose"
    }
}
