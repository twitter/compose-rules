// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.rules.core.detekt

import com.twitter.rules.core.ComposeKtConfig
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.valueOrDefaultCommaSeparated

/**
 * Manages the configuration for detekt rules. Results will be memoized, as config shouldn't be changing
 * during the lifetime of a rule.
 */
internal class DetektComposeKtConfig(
    private val config: Config,
) : ComposeKtConfig {
    private val cache = mutableMapOf<String, Any?>()

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> valueOrPut(key: String, value: () -> T?): T? =
        cache.getOrPut(key) { value() } as? T

    override fun getInt(key: String, default: Int): Int =
        valueOrPut(key) { config.valueOrDefault(key, default) } ?: default

    override fun getString(key: String, default: String?): String? =
        valueOrPut(key) {
            if (default == null) {
                config.valueOrNull(key)
            } else {
                config.valueOrDefault(key, default)
            }
        }

    override fun getList(key: String, default: List<String>): List<String> =
        valueOrPut(key) { config.valueOrDefaultCommaSeparated(key, default) } ?: default

    override fun getSet(key: String, default: Set<String>): Set<String> =
        valueOrPut(key) { getList(key, default.toList()).toSet() } ?: default

    override fun getBoolean(key: String, default: Boolean): Boolean =
        valueOrPut(key) { config.valueOrDefault(key, default) } ?: default
}
