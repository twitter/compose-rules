// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.rules.core.ktlint

import com.pinterest.ktlint.core.api.UsesEditorConfigProperties
import org.ec4j.core.model.PropertyType

val contentEmittersProperty: UsesEditorConfigProperties.EditorConfigProperty<String> =
    UsesEditorConfigProperties.EditorConfigProperty(
        type = PropertyType.LowerCasingPropertyType(
            "content_emitters",
            "A comma separated list of composable functions that emit content (e.g. UI)",
            PropertyType.PropertyValueParser.IDENTITY_VALUE_PARSER,
            emptySet()
        ),
        defaultValue = "",
        propertyMapper = { property, _ ->
            when {
                property?.isUnset == true -> ""
                property?.getValueAs<String>() != null -> property.getValueAs<String>()
                else -> property?.getValueAs()
            }
        }
    )

val compositionLocalAllowlistProperty: UsesEditorConfigProperties.EditorConfigProperty<String> =
    UsesEditorConfigProperties.EditorConfigProperty(
        type = PropertyType.LowerCasingPropertyType(
            "allowed_composition_locals",
            "A comma separated list of allowed CompositionLocals",
            PropertyType.PropertyValueParser.IDENTITY_VALUE_PARSER,
            emptySet()
        ),
        defaultValue = "",
        propertyMapper = { property, _ ->
            when {
                property?.isUnset == true -> ""
                property?.getValueAs<String>() != null -> property.getValueAs<String>()
                else -> property?.getValueAs()
            }
        }
    )
