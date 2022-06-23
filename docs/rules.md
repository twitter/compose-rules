## State

### Hoist all the things

Compose is built upon the idea of a [unidirectional data flow](https://developer.android.com/jetpack/compose/state#state-hoisting), which can be summarised as: data/state flows down, and events fire up. To implement that, Compose advocates for the pattern of [hoisting state](https://developer.android.com/jetpack/compose/state#state-hoisting) upwards, enabling the majority of your composable functions to be stateless. This has many benefits, including far easier testing.

In practice, there are a few common things to look out for:
- Do not pass ViewModels (or objects from DI) down.
- Do not pass `State<Foo>` or `MutableState<Bar>` instances down.

Instead pass down the relevant data to the function, and optional lambdas for callbacks.

More information: [State and Jetpack Compose](https://developer.android.com/jetpack/compose/state)

Related rule: [twitter-compose:vm-forwarding-check](https://github.com/twitter/compose-rules/blob/main/rules/ktlint/src/main/kotlin/com/twitter/rules/ktlint/compose/ComposeViewModelForwardingCheck.kt)

### State should be remembered in composables

Be careful when using `mutableStateOf` (or any of the other state builders) to make sure that you `remember` the instance. If you don't `remember` the state instance, a new state instance will be created when the function is recomposed.

Related rule: [twitter-compose:remember-missing-check](https://github.com/twitter/compose-rules/blob/main/rules/ktlint/src/main/kotlin/com/twitter/rules/ktlint/compose/ComposeRememberMissingCheck.kt)

### Use Immutable annotation whenever possible

The Compose Compiler tries to infer immutability and stability on value classes, but sometimes it gets it wrong, which then means that your UI will be doing more work than it needs. To force the compiler to see a class as 'immutable' you can apply the `@Immutable` annotation to the class.

More info: [Immutable docs](https://developer.android.com/reference/kotlin/androidx/compose/runtime/Immutable) and [Composable metrics blog post](https://chris.banes.dev/composable-metrics/)

Related rule: TBD

## Composables

### Do not use inherently mutable types as parameters

This practice follows on from the 'Hoist all the things' item above, where we said that state flows down. It might be tempting to pass mutable state down to a function to mutate the value.

This is an anti-pattern though as it breaks the pattern of state flowing down, and events firing up. The mutation of the value is an event which should be modelled within the function API (a lambda callback).

There are a few reasons for this, but the main one is that it is very easy to use a mutable object which does not trigger recomposition. Without triggering recomposition, your composables will not automatically update to reflect the updated value.

Passing `ArrayList<T>`, `MutableState<T>`, `ViewModel` are common examples of this (but not limited to those types).

Related rule: [twitter-compose:mutable-params-check](https://github.com/twitter/compose-rules/blob/main/rules/ktlint/src/main/kotlin/com/twitter/rules/ktlint/compose/ComposeMutableParametersCheck.kt)

### Do not emit content and return a result

Composable functions should either emit layout content, or return a value, but not both.

If a composable should offer additional control surfaces to its caller, those control surfaces or callbacks should be provided as parameters to the composable function by the caller.

More info: [Compose API guidelines](https://github.com/androidx/androidx/blob/androidx-main/compose/docs/compose-api-guidelines.md#emit-xor-return-a-value)

Related rule: [twitter-compose:multiple-emitters-check](https://github.com/twitter/compose-rules/blob/main/rules/ktlint/src/main/kotlin/com/twitter/rules/ktlint/compose/ComposeMultipleContentEmittersCheck.kt)

### Do not emit multiple pieces of content

A composable function should emit either 0 or 1 pieces of layout, but no more. A composable function should be cohesive, and not rely on what function it is called from.

You can see an example of what not to do below. `InnerContent()` emits a number of layout nodes and assumes that it will be called from a Column:

```kotlin
Column {
    InnerContent()
}

@Composable
private fun InnerContent() {
    Text(...)
    Image(...)
    Button(...)
}
```

However InnerContent could just as easily be called from a Row which would break all assumptions. Instead, InnerContent should be cohesive and emit a single layout node itself:

```kotlin
@Composable
private fun InnerContent() {
    Column {
        Text(...)
        Image(...)
        Button(...)
    }
}
```
Nesting of layouts has a drastically lower cost vs the view system, so developers should not try to minimize UI layers at the cost of correctness.

There is a slight exception to this rule, which is when the function is defined as an extension function of an appropriate scope, like so:
```kotlin
@Composable
private fun ColumnScope.InnerContent() {
    Text(...)
    Image(...)
    Button(...)
}
```
This effectively ties the function to be called from a Column, but is still not recommended (although permitted).

Related rule: [twitter-compose:multiple-emitters-check](https://github.com/twitter/compose-rules/blob/main/rules/ktlint/src/main/kotlin/com/twitter/rules/ktlint/compose/ComposeMultipleContentEmittersCheck.kt)

### Naming @Composable functions properly

Composable functions that return `Unit` should start with an uppercase letter. They are considered declarative entities that can be either present or absent in a composition and therefore follow the naming rules for classes.

However, Composable functions that return a value should start with a lowercase letter instead. They should follow the standard [Kotlin Coding Conventions](https://kotlinlang.org/docs/reference/coding-conventions.html#function-names) for the naming of functions for any function annotated `@Composable` that returns a value other than `Unit`

More information: [Naming Unit @Composable functions as entities](https://github.com/androidx/androidx/blob/androidx-main/compose/docs/compose-api-guidelines.md#naming-unit-composable-functions-as-entities) and [Naming @Composable functions that return values](https://github.com/androidx/androidx/blob/androidx-main/compose/docs/compose-api-guidelines.md#naming-composable-functions-that-return-values)

Related rule: [twitter-compose:naming-check](https://github.com/twitter/compose-rules/blob/main/rules/ktlint/src/main/kotlin/com/twitter/rules/ktlint/compose/ComposeNamingCheck.kt)

### Make dependencies explicit

When designing our composables, we should always try to be explicit about the dependencies they take in. If you acquire a ViewModel or an instance from DI in the body of the composable, you are making this dependency implicit, which has the downsides of making it hard to test and harder to reuse.

To solve this problem, you should inject these dependencies as default values in the composable function.

Let's see it with an example.

```kotlin
@Composable
private fun MyComposable() {
    val viewModel = weaverViewModel<MyViewModel>()
    // ...
}
```
In this composable, the dependencies are implicit. When testing it you would need to fake the internals of viewModel somehow to be able to acquire your intended ViewModel.

But, if you change it to pass these instances via the composable function parameters, you could provide the instance you want directly in your tests without any extra effort. It would also have the upside of the function being explicit about its external dependencies in its signature.

```kotlin
@Composable
private fun MyComposable(
    viewModel: MyViewModel = viewModel(),
) {
    // ...
}

```

Related rule: [twitter-compose:vm-injection-check](https://github.com/twitter/compose-rules/blob/main/rules/ktlint/src/main/kotlin/com/twitter/rules/ktlint/compose/ComposeViewModelInjectionCheck.kt)

## Modifiers

### When should I expose modifier parameters?

Modifiers are the beating heart of Compose UI. They encapsulate the idea of composition over inheritance, by allowing developers to attach logic and behavior to layouts.

They are especially important for your public components, as they allow callers to customize the component to their wishes.

More info: [Always provide a Modifier parameter](https://chris.banes.dev/always-provide-a-modifier/)

Related rule: [twitter-compose:modifier-check](https://github.com/twitter/compose-rules/blob/main/rules/ktlint/src/main/kotlin/com/twitter/rules/ktlint/compose/ComposeModifierMissingCheck.kt)

### Don't re-use modifiers

Modifiers which are passed in are designed so that they should be used by a single layout node in the composable function. If the provided modifier is used by multiple composables at different levels, unwanted behaviour can happen.

In the following example we've exposed a public modifier parameter, and then passed it to the root Column, but we've also passed it to each of the descendant calls, with some extra modifiers on top:

```kotlin
@Composable
private fun InnerContent(modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(modifier.clickable(), ...)
        Image(modifier.size(), ...)
        Button(modifier, ...)
    }
}
```
This is not recommended. Instead, the provided modifier should only be used on the Column. The descendant calls should use newly built modifiers, by using the empty Modifier object:

```kotlin
@Composable
private fun InnerContent(modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(Modifier.clickable(), ...)
        Image(Modifier.size(), ...)
        Button(Modifier, ...)
    }
}
```

Related rule: [twitter-compose:modifier-used-once-check](https://github.com/twitter/compose-rules/blob/main/rules/ktlint/src/main/kotlin/com/twitter/rules/ktlint/compose/ComposeModifierUsedOnceCheck.kt)
