# Twitter's Jetpack Compose Rules


![build](https://github.com/twitter/compose-rules/actions/workflows/build.yaml/badge.svg) ![Latest version](https://img.shields.io/maven-central/v/com.twitter.compose.rules/common)

It can be challenging for big teams to start adopting Compose-UI, particularly because not everyone will start at same time or with the same patterns. Twitter tried to ease the pain by creating a set of Compose static checks.

Compose has lots of superpowers but also has a bunch of footguns to be aware of [as seen in this Twitter Thread](https://twitter.com/mrmans0n/status/1507390768796909571).

This is where our static checks come in. We want to detect as many potential issues as we can, as quickly as we can. In this case we want an error to show prior to engineers having to review code. Similar to other static check libraries we hope this leads to a "don't shoot the messengers" philosphy which will foster healthy Compose adoption.

Check out the project website for more information: https://twitter.github.io/compose-rules

## Static checks for Compose bundled in this repo

You can find the comprehensive list of rules in [the rules documentaton](https://twitter.github.io/compose-rules/rules). It contains both the what and why for each rule, we encourage you to read it prior to adopting our rules.

There are 2 ways you can add these static checks to your build - using ktlint or using Detekt.

## Using with ktlint

```groovy
dependencies {
    ktlintRuleset "com.twitter.compose.rules:ktlint:<VERSION>"
}
```

For more information, you can refer to the [Using with ktlint](https://twitter.github.io/compose-rules/#using-the-custom-ruleset-with-ktlint) documentation.

## Using with Detekt

```groovy
dependencies {
    detektPlugins "com.twitter.compose.rules:detekt:<VERSION>"
}
```

For more information, you can refer to the [Using with Detekt](https://twitter.github.io/compose-rules/#using-the-custom-ruleset-with-detekt) documentation.

## Contributing

We love sharing and learning from others; contributing new rules or fixes is welcome. See the [Contributing](CONTRIBUTING.md) instructions for more information.

## License

```
    Copyright 2022 Twitter, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
```
