// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.rules.core.detekt

import com.twitter.rules.core.ComposeKtConfig
import com.twitter.rules.core.ComposeKtConfig.Companion.attach
import com.twitter.rules.core.ComposeKtVisitor
import com.twitter.rules.core.Emitter
import com.twitter.rules.core.util.isComposable
import com.twitter.rules.core.util.runIf
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.CorrectableCodeSmell
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Location
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.com.intellij.psi.PsiNameIdentifierOwner
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes
import org.jetbrains.kotlin.utils.addToStdlib.cast

abstract class TwitterDetektRule(
    config: Config = Config.empty,
) : Rule(config), ComposeKtVisitor {

    private val config: ComposeKtConfig by lazy { DetektComposeKtConfig(this) }

    private val emitter: Emitter = Emitter { element, message, canBeAutoCorrected ->
        // Grab the named element if there were any, otherwise fall back to the whole PsiElement
        val finalElement = element.runIf(element is PsiNameIdentifierOwner) {
            (this as PsiNameIdentifierOwner).nameIdentifier!!
        }
        val finding = when {
            canBeAutoCorrected -> CorrectableCodeSmell(
                issue = issue,
                entity = Entity.from(finalElement, Location.from(finalElement)),
                message = message,
                autoCorrectEnabled = autoCorrect,
            )

            else -> CodeSmell(
                issue = issue,
                entity = Entity.from(finalElement, Location.from(finalElement)),
                message = message,
            )
        }
        report(finding)
    }

    override fun visit(root: KtFile) {
        super.visit(root)
        root.attach(config)
        visitFile(root, autoCorrect, emitter)
    }

    override fun visitClass(klass: KtClass) {
        super<Rule>.visitClass(klass)
        klass.attach(config)
        visitClass(klass, autoCorrect, emitter)
    }

    override fun visitKtElement(element: KtElement) {
        super.visitKtElement(element)
        element.attach(config)
        when (element.node.elementType) {
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
