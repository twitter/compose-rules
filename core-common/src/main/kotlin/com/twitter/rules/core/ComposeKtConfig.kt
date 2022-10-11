// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.rules.core

import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.com.intellij.psi.PsiElement

interface ComposeKtConfig {
    fun getInt(key: String, default: Int): Int
    fun getString(key: String, default: String?): String?
    fun getList(key: String, default: List<String>): List<String>
    fun getSet(key: String, default: Set<String>): Set<String>
    fun getBoolean(key: String, default: Boolean): Boolean

    companion object {
        private val Key: Key<ComposeKtConfig> = Key("twitter_compose_rules_config")
        private val ReturnDefaults = object : ComposeKtConfig {
            override fun getInt(key: String, default: Int): Int = default
            override fun getString(key: String, default: String?): String? = default
            override fun getList(key: String, default: List<String>): List<String> = default
            override fun getSet(key: String, default: Set<String>) = default
            override fun getBoolean(key: String, default: Boolean) = default
        }

        fun PsiElement.config(): ComposeKtConfig = containingFile.getUserData(Key) ?: ReturnDefaults

        fun ASTNode.config(): ComposeKtConfig = psi.config()

        fun PsiElement.attach(config: ComposeKtConfig) {
            containingFile.putUserData(Key, config)
        }
    }
}
