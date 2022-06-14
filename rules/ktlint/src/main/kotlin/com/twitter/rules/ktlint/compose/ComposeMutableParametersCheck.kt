package com.twitter.rules.ktlint.compose

import com.twitter.rules.core.isComposable
import com.twitter.rules.core.isTypeMutable
import com.twitter.rules.core.ktlint.Emitter
import com.twitter.rules.core.ktlint.TwitterKtRule
import com.twitter.rules.core.ktlint.report
import org.jetbrains.kotlin.psi.KtFunction

class ComposeMutableParametersCheck : TwitterKtRule("compose-mutable-params-check") {

    override fun visitFunction(function: KtFunction, autoCorrect: Boolean, emitter: Emitter) {
        if (!function.isComposable) return

        function.valueParameters
            .filter { it.isTypeMutable }
            .forEach { emitter.report(it, MutableParameterInCompose) }
    }

    companion object {

        val MutableParameterInCompose = """
            Using mutable objects as state in Compose will cause your users to see incorrect or stale data in your app.
            Mutable objects that are not observable, such as ArrayList<T> or a mutable data class, cannot be observed by
            Compose to trigger recomposition when they change.

            See https://github.com/twitter/compose-ktlint-rules/blob/main/docs/rules.md#do-not-use-inherently-mutable-types-as-parameters for more information.
        """.trimIndent()
    }
}
