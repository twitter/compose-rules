Twitter Compose Rules is a set of custom ktlint rules to ensure that your composables don't fall into common pitfalls, that might be easy to miss in code reviews.

## Why

A big challenge to face when a big team with a large codebase starts adopting Compose is that not everybody will start at the same page. This happened to us at Twitter.

Compose is 🔝, allows for amazing things, but has a bunch of footguns to be aware of. [See the thread](https://twitter.com/mrmans0n/status/1507390768796909571).

This is where these ktlint rules come in. We want to detect all the potential issues even before they reach the code review state, and foster a healthy Compose adoption.

## Disabling a specific rule

To disable a rule you have to follow the [instructions from the ktlint documentation](https://github.com/pinterest/ktlint#how-do-i-suppress-an-errors-for-a-lineblockfile), and use the id of the rule you want to disable with the `twitter-compose` tag.

For example, to disable `compose-naming-check`, the tag you'll need to disable is `twitter-compose:compose-naming-check`.

```kotlin
    /* ktlint-disable twitter-compose:compose-naming-check */
    ... your code here
```
