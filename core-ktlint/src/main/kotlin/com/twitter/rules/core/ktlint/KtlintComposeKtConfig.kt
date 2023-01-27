// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.rules.core.ktlint

import com.pinterest.ktlint.core.api.EditorConfigProperties
import com.twitter.rules.core.ComposeKtConfig
import com.twitter.rules.core.util.toSnakeCase

/**
 * Manages the configuration for ktlint rules. In ktlint, configs are typically in snake case, while in the
 * whole project and in detekt they are camel case, so this class will convert all camel case keys to snake case,
 * and add a "twitter_compose_" prefix to all of them.
 * Results will be memoized as well, as config shouldn't be changing during the lifetime of a rule.
 */
internal class KtlintComposeKtConfig(
    private val properties: EditorConfigProperties,
) : ComposeKtConfig {
    private val cache = mutableMapOf<String, Any?>()

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> getValueAsOrPut(key: String, value: () -> T?): T? =
        cache.getOrPut(key) { value() } as? T

    override fun getInt(key: String, default: Int): Int =
        getValueAsOrPut(key) { properties[ktlintKey(key)]?.getValueAs<String>()?.toInt() } ?: default

    override fun getString(key: String, default: String?): String? =
        getValueAsOrPut(key) { properties[ktlintKey(key)]?.getValueAs() } ?: default

    override fun getList(key: String, default: List<String>): List<String> =
        getValueAsOrPut(key) {
            val original = properties[ktlintKey(key)]?.getValueAs<String>() ?: return@getValueAsOrPut default
            original.split(',', ';').map { it.trim() }
        } ?: default

    override fun getSet(key: String, default: Set<String>): Set<String> =
        getValueAsOrPut(key) { getList(key, default.toList()).toSet() } ?: default

    override fun getBoolean(key: String, default: Boolean): Boolean =
        getValueAsOrPut(key) { properties[ktlintKey(key)]?.getValueAs<Boolean>() } ?: default

    private fun ktlintKey(key: String): String = "twitter_compose_${key.toSnakeCase()}"
}
