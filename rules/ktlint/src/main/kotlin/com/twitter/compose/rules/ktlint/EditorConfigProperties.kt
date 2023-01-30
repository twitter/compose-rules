// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules.ktlint

import com.pinterest.ktlint.core.api.editorconfig.EditorConfigProperty
import org.ec4j.core.model.PropertyType
import org.ec4j.core.model.PropertyType.PropertyValueParser

val contentEmittersProperty: EditorConfigProperty<String> =
    EditorConfigProperty(
        type = PropertyType.LowerCasingPropertyType(
            "twitter_compose_content_emitters",
            "A comma separated list of composable functions that emit content (e.g. UI)",
            PropertyType.PropertyValueParser.IDENTITY_VALUE_PARSER,
            emptySet(),
        ),
        defaultValue = "",
        propertyMapper = { property, _ ->
            when {
                property?.isUnset == true -> ""
                property?.getValueAs<String>() != null -> property.getValueAs<String>()
                else -> property?.getValueAs()
            }
        },
    )

val compositionLocalAllowlistProperty: EditorConfigProperty<String> =
    EditorConfigProperty(
        type = PropertyType.LowerCasingPropertyType(
            "twitter_compose_allowed_composition_locals",
            "A comma separated list of allowed CompositionLocals",
            PropertyType.PropertyValueParser.IDENTITY_VALUE_PARSER,
            emptySet(),
        ),
        defaultValue = "",
        propertyMapper = { property, _ ->
            when {
                property?.isUnset == true -> ""
                property?.getValueAs<String>() != null -> property.getValueAs<String>()
                else -> property?.getValueAs()
            }
        },
    )

val previewPublicOnlyIfParams: EditorConfigProperty<Boolean> =
    EditorConfigProperty(
        type = PropertyType.LowerCasingPropertyType(
            "twitter_compose_preview_public_only_if_params",
            "If set to true, it means ",
            //
            PropertyValueParser.BOOLEAN_VALUE_PARSER,
            "true",
            "false",
        ),
        defaultValue = true,
    )

val allowedComposeNamingNames: EditorConfigProperty<String> =
    EditorConfigProperty(
        type = PropertyType.LowerCasingPropertyType(
            "twitter_compose_allowed_composable_function_names",
            "A comma separated list of regexes of allowed composable function names",
            PropertyType.PropertyValueParser.IDENTITY_VALUE_PARSER,
            emptySet(),
        ),
        defaultValue = "",
        propertyMapper = { property, _ ->
            when {
                property?.isUnset == true -> ""
                property?.getValueAs<String>() != null -> property.getValueAs<String>()
                else -> property?.getValueAs()
            }
        },
    )
