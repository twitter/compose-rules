// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.rules.core.ktlint

import com.pinterest.ktlint.core.api.UsesEditorConfigProperties
import org.ec4j.core.model.PropertyType
import org.ec4j.core.model.PropertyType.PropertyValueParser

val contentEmittersProperty: UsesEditorConfigProperties.EditorConfigProperty<String> =
    UsesEditorConfigProperties.EditorConfigProperty(
        type = PropertyType.LowerCasingPropertyType(
            "twitter_compose_content_emitters",
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
            "twitter_compose_allowed_composition_locals",
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

val previewPublicOnlyIfParams: UsesEditorConfigProperties.EditorConfigProperty<Boolean> =
    UsesEditorConfigProperties.EditorConfigProperty(
        type = PropertyType.LowerCasingPropertyType(
            "twitter_compose_preview_public_only_if_params",
            "If set to true, it means ",
            //
            PropertyValueParser.BOOLEAN_VALUE_PARSER,
            "true",
            "false"
        ),
        defaultValue = true
    )
