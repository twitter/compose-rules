// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules

import com.twitter.rules.core.ComposeKtVisitor
import com.twitter.rules.core.Emitter
import com.twitter.rules.core.report
import com.twitter.rules.core.util.isPreview
import com.twitter.rules.core.util.isPreviewAnnotation
import org.jetbrains.kotlin.psi.KtClass

class ComposePreviewNaming : ComposeKtVisitor {
    override fun visitClass(clazz: KtClass, autoCorrect: Boolean, emitter: Emitter) {
        if (!clazz.isAnnotation()) return
        if (!clazz.isPreview) return

        // We know here that we are in an annotation that either has a @Preview or other preview annotations
        val count = clazz.annotationEntries.count { it.isPreviewAnnotation }
        val name = clazz.nameAsSafeName.asString()
        if (count == 1 && !name.endsWith("Preview")) {
            emitter.report(clazz, createMessage(count, "Preview"))
        } else if (count > 1 && !name.endsWith("Previews")) {
            emitter.report(clazz, createMessage(count, "Previews"))
        }
    }

    companion object {
        fun createMessage(count: Int, suggestedSuffix: String): String = """
            Preview annotations with $count preview annotations should end with the `$suggestedSuffix` suffix.

            See https://twitter.github.io/compose-rules/rules/#naming-multipreview-annotations-properly for more information.
        """.trimIndent()
    }
}
