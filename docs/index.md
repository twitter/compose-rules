Twitter Compose Rules is a set of custom ktlint rules to ensure that your composables don't fall into common pitfalls, that might be easy to miss in code reviews.

## Why

A big challenge to face when a big team with a large codebase starts adopting Compose is that not everybody will start at the same page. This happened to us at Twitter.

Compose is 🔝, allows for amazing things, but has a bunch of footguns to be aware of. [See the thread](https://twitter.com/mrmans0n/status/1507390768796909571).

This is where these static checks come in. We want to detect all the potential issues even before they reach the code review state, and foster a healthy Compose adoption.

## Using the custom ruleset with ktlint

### With ktlint-gradle

If using [ktlint-gradle](https://github.com/JLLeitschuh/ktlint-gradle), you can specify the dependency on this set of rules by using the `ktlintRuleset`.

```groovy
dependencies {
    ktlintRuleset "com.twitter.compose.rules:ktlint:<VERSION>"
}
```

NOTE: Currently [there seems to be an issue supporting ktlint over 0.46.0](https://github.com/JLLeitschuh/ktlint-gradle/pull/595).

### With spotless

If using [Spotless](https://github.com/diffplug/spotless), there is currently a workaround on how to do that described [in this issue](https://github.com/diffplug/spotless/issues/1220).


### Disabling a specific rule

To disable a rule you have to follow the [instructions from the ktlint documentation](https://github.com/pinterest/ktlint#how-do-i-suppress-an-errors-for-a-lineblockfile), and use the id of the rule you want to disable with the `twitter-compose` tag.

For example, to disable `compose-naming-check`, the tag you'll need to disable is `twitter-compose:compose-naming-check`.

```kotlin
    /* ktlint-disable twitter-compose:compose-naming-check */
    ... your code here
    /* ktlint-enable twitter-compose:compose-naming-check */
```

## Using the custom ruleset with Detekt

When using the [Detekt Gradle Plugin](https://detekt.dev/docs/gettingstarted/gradle), you can specify the dependency on this set of rules by using `detektPlugins`.

```groovy
dependencies {
    detektPlugins "com.twitter.compose.rules:detekt:<VERSION>"
}
```

### Enabling rules

For the rules to be picked up, you will need to enable them in your `detekt.yml` file.

```yaml
TwitterCompose:
  ContentEmitterReturningValues:
    active: true
  ModifierComposable:
    active: true
  ModifierMissing:
    active: true
  ModifierReused:
    active: true
  ModifierWithoutDefault:
    active: true
  MultipleEmitters:
    active: true
  MutableParams:
    active: true
  ComposableNaming:
    active: true
  ComposableParamOrder:
    active: true
  PreviewPublic:
    active: true
  RememberMissing:
    active: true
  ViewModelForwarding:
    active: true
  ViewModelInjection:
    active: true
```

### Disabling a specific rule

To disable a rule you have to follow the [instructions from the Detekt documentation](https://detekt.dev/docs/introduction/suppressing-rules), and use the id of the rule you want to disable.

For example, to disable `ComposableNaming`:

```kotlin
@Suppress("ComposableNaming")
@Composable
fun myNameIsWrong() { }
```
