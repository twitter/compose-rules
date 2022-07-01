package com.twitter.rules.ktlint.compose

import com.twitter.rules.core.Emitter
import com.twitter.rules.core.ktlint.TwitterKtlintRule
import com.twitter.rules.core.report
import com.twitter.rules.core.util.isTypeMutable
import org.jetbrains.kotlin.psi.KtFunction

class ComposeMutableParametersCheck : TwitterKtlintRule("twitter-compose:mutable-params-check") {

    override fun visitComposable(function: KtFunction, autoCorrect: Boolean, emitter: Emitter) {
        function.valueParameters
            .filter { it.isTypeMutable }
            .forEach { emitter.report(it, MutableParameterInCompose) }
    }

    companion object {

        val MutableParameterInCompose = """
            Using mutable objects as state in Compose will cause your users to see incorrect or stale data in your app.
            Mutable objects that are not observable, such as ArrayList<T> or a mutable data class, cannot be observed by
            Compose to trigger recomposition when they change.

            See https://github.com/twitter/compose-rules/blob/main/docs/rules.md#do-not-use-inherently-mutable-types-as-parameters for more information.
        """.trimIndent()
    }
}
