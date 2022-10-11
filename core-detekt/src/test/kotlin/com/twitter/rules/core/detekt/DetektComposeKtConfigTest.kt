// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.rules.core.detekt

import io.gitlab.arturbosch.detekt.test.TestConfig
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test

class DetektComposeKtConfigTest {

    private val mapping = mutableMapOf<String, Any>().apply {
        put("myInt", 10)
        put("myString", "abcd")
        put("myList", "a,b,c,a")
        put("myList2", "a , b , c,a")
        put("mySet", "a,b,c,a,b,c")
        put("mySet2", "  a, b,c ,a  , b  ,  c ")
        put("myBool", true)
    }

    private val detektConfig = TestConfig(mapping)
    private val config = DetektComposeKtConfig(detektConfig)

    @Test
    fun `returns ints from Config, and default values when unset`() {
        assertThat(config.getInt("myInt", 0)).isEqualTo(10)
        assertThat(config.getInt("myOtherInt", 0)).isEqualTo(0)
    }

    @Test
    fun `returns strings from Config, and default values when unset`() {
        assertThat(config.getString("myString", null)).isEqualTo("abcd")
        assertThat(config.getString("myOtherString", "ABCD")).isEqualTo("ABCD")
        assertThat(config.getString("myOtherStringWithNullDefault", null)).isNull()
    }

    @Test
    fun `returns lists from Config, and default values when unset`() {
        assertThat(config.getList("myList", emptyList())).containsExactly("a", "b", "c", "a")
        assertThat(config.getList("myList2", emptyList())).containsExactly("a", "b", "c", "a")
        assertThat(config.getList("myOtherList", listOf("a"))).containsExactly("a")
    }

    @Test
    fun `returns sets from Config, and default values when unset`() {
        assertThat(config.getSet("mySet", emptySet())).containsExactly("a", "b", "c")
        assertThat(config.getSet("mySet2", emptySet())).containsExactly("a", "b", "c")
        assertThat(config.getSet("myOtherSet", setOf("a"))).containsExactly("a")
    }

    @Test
    fun `returns booleans from Config, and default values when unset`() {
        assertThat(config.getBoolean("myBool", false)).isTrue()
        assertThat(config.getBoolean("myOtherBool", false)).isFalse()
    }

    @Test
    fun `results are memoized`() {
        assertThat(config.getInt("myInt", 0)).isEqualTo(10)
        assertThat(config.getString("myString", null)).isEqualTo("abcd")
        assertThat(config.getList("myList", emptyList())).containsExactly("a", "b", "c", "a")
        assertThat(config.getList("myList2", emptyList())).containsExactly("a", "b", "c", "a")
        assertThat(config.getSet("mySet", emptySet())).containsExactly("a", "b", "c")
        assertThat(config.getSet("mySet2", emptySet())).containsExactly("a", "b", "c")

        mapping["myInt"] = 100
        mapping["myString"] = "XYZ"
        mapping["myList"] = "z,y,x"
        mapping["myList2"] = "z,y"
        mapping["mySet"] = "a"
        mapping["mySet2"] = "a, b"

        assertThat(config.getInt("myInt", 0)).isEqualTo(10)
        assertThat(config.getString("myString", null)).isEqualTo("abcd")
        assertThat(config.getList("myList", emptyList())).containsExactly("a", "b", "c", "a")
        assertThat(config.getList("myList2", emptyList())).containsExactly("a", "b", "c", "a")
        assertThat(config.getSet("mySet", emptySet())).containsExactly("a", "b", "c")
        assertThat(config.getSet("mySet2", emptySet())).containsExactly("a", "b", "c")
    }
}
