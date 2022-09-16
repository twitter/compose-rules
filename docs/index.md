Twitter Compose Rules is a set of custom ktlint rules to ensure that your composables don't fall into common pitfalls, that might be easy to miss in code reviews.

## Why
It can be challenging for big teams to start adopting Compose, particularly because not everyone will start at same time or with the same patterns. Twitter tried to ease the pain by creating a set of Compose static checks.

Compose has lots of superpowers but also has a bunch of footguns to be aware of [as seen in this Twitter Thread](https://twitter.com/mrmans0n/status/1507390768796909571).

This is where our static checks come in. We want to detect as many potential issues as we can, as quickly as we can. In this case we want an error to show prior to engineers having to review code. Similar to other static check libraries we hope this leads to a "don't shoot the messengers" philosphy which will foster healthy Compose adoption.

## Using with ktlint

You can refer to the [Using with ktlint](https://twitter.github.io/compose-rules/ktlint) documentation.

## Using with Detekt

You can refer to the [Using with Detekt](https://twitter.github.io/compose-rules/detekt) documentation.
