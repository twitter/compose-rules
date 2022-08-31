// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.rules.core.detekt

import com.twitter.rules.core.Emitter
import com.twitter.rules.core.KtElementVisitors
import com.twitter.rules.core.util.isComposable
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.CorrectableCodeSmell
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Location
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes
import org.jetbrains.kotlin.utils.addToStdlib.cast

abstract class TwitterDetektRule(
    config: Config = Config.empty
) : Rule(config), KtElementVisitors {

    private val emitter: Emitter = Emitter { element, message, canBeAutoCorrected ->
        val finding = when {
            canBeAutoCorrected -> CorrectableCodeSmell(
                issue = issue,
                entity = Entity.from(element, Location.from(element)),
                message = message,
                autoCorrectEnabled = autoCorrect
            )

            else -> CodeSmell(
                issue = issue,
                entity = Entity.from(element, Location.from(element)),
                message = message
            )
        }
        report(finding)
    }

    override fun visitKtElement(element: KtElement) {
        super.visitKtElement(element)
        when (element.node.elementType) {
            KtStubElementTypes.FILE -> visitFile(element.cast(), autoCorrect, emitter)
            KtStubElementTypes.CLASS -> visitClass(element.cast(), autoCorrect, emitter)
            KtStubElementTypes.FUNCTION -> {
                val function = element.cast<KtFunction>()
                visitFunction(function, autoCorrect, emitter)
                if (function.isComposable) {
                    visitComposable(function, autoCorrect, emitter)
                }
            }
        }
    }
}
