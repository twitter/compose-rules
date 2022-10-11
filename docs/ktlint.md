## Using with Kotlinter

If using [kotlinter](https://github.com/jeremymailen/kotlinter-gradle), you can specify the dependency on this set of rules [by using the `buildscript` classpath](https://github.com/jeremymailen/kotlinter-gradle#custom-rules).

```groovy
buildscript {
    dependencies {
        classpath "com.twitter.compose.rules:ktlint:<version>"
    }
}
```

## Using with ktlint-gradle

> **Warning**: This plugin doesn't currently support ktlint versions over 0.46.0, [they are working to support it right now](https://github.com/JLLeitschuh/ktlint-gradle/pull/595).

If using [ktlint-gradle](https://github.com/JLLeitschuh/ktlint-gradle), you can specify the dependency on this set of rules by using the `ktlintRuleset`.

```groovy
dependencies {
    ktlintRuleset "com.twitter.compose.rules:ktlint:<VERSION>"
}
```

## Using with spotless

> **Warning**: If using [Spotless](https://github.com/diffplug/spotless), there is [no current way of enabling a custom ruleset like ours](https://github.com/diffplug/spotless/issues/1220). You would need to use any of the alternatives listed here (like Kotlinter) to just run these rules.

## Using with ktlint CLI or the ktlint (unofficial) IntelliJ plugin

The [releases](https://github.com/twitter/compose-rules/releases) page contains an [uber jar](https://stackoverflow.com/questions/11947037/what-is-an-uber-jar) for each version release that can be used for these purposes.

To use with [ktlint CLI](https://ktlint.github.io/#getting-started):
```shell
ktlint -R ktlint-twitter-compose-<VERSION>-all.jar
```

You can use this same jar in the [ktlint (unofficial) IntelliJ plugin](https://plugins.jetbrains.com/plugin/15057-ktlint-unofficial-) if the rules are compiled against the same ktlint version used for that release. You can configure the custom ruleset in the preferences page of the plugin.

## Configuring rules

### Providing custom content emitters

There are some rules (`twitter-compose:content-emitter-returning-values-check` and `twitter-compose:multiple-emitters-check`) that use predefined list of known composables that emit content. But you can add your own too! In your `.editorconfig` file, you'll need to add a `content_emitters` property followed by a list of composable names separated by commas. You would typically want the composables that are part of your custom design system to be in this list.

```editorconfig
[*.{kt,kts}]
twitter_compose_content_emitters = MyComposable,MyOtherComposable
```

### Providing a list of allowed `CompositionLocal`s

For `compositionlocal-allowlist` rule you can define a list of `CompositionLocal`s that are allowed in your codebase.

```editorconfig
[*.{kt,kts}]
twitter_compose_allowed_composition_locals = LocalSomething,LocalSomethingElse
```

### Make it so that all @Preview composables must be not public, no exceptions

In `preview-public-check`, only previews with a `@PreviewParameter` are required to be non-public by default. However, if you want to make it so ALL `@Preview` composables are non-public, you can add this to your `.editorconfig` file:

```editorconfig
[*.{kt,kts}]
twitter_compose_preview_public_only_if_params = false
```

## Disabling a specific rule

To disable a rule you have to follow the [instructions from the ktlint documentation](https://github.com/pinterest/ktlint#how-do-i-suppress-an-errors-for-a-lineblockfile), and use the id of the rule you want to disable with the `twitter-compose` tag.

For example, to disable `compose-naming-check`, the tag you'll need to disable is `twitter-compose:compose-naming-check`.

```kotlin
    /* ktlint-disable twitter-compose:compose-naming-check */
    ... your code here
    /* ktlint-enable twitter-compose:compose-naming-check */
```
