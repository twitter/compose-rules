// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules.ktlint

import com.pinterest.ktlint.test.KtLintAssertThat.Companion.assertThatRule
import com.pinterest.ktlint.test.LintViolation
import com.twitter.compose.rules.ComposeModifierReused
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

class ComposeModifierReusedCheckTest {

    private val modifierRuleAssertThat = assertThatRule { ComposeModifierReusedCheck() }

    @Test
    fun `errors when the modifier parameter of a Composable is used more than once by siblings or parent-children`() {
        @Language("kotlin")
        val code =
            """
                @Composable
                fun Something(modifier: Modifier) {
                    Row(modifier) {
                        SomethingElse(modifier)
                    }
                }
                @Composable
                fun Something(modifier: Modifier): Int {
                    Column(modifier = modifier) {
                        SomethingElse(modifier = Modifier)
                        SomethingDifferent(modifier = modifier)
                    }
                }
                @Composable
                fun BoxScope.Something(modifier: Modifier) {
                    Column(modifier = modifier) {
                        SomethingDifferent()
                    }
                    SomethingElse(modifier = modifier)
                    SomethingElse(modifier = modifier.padding12())
                }
                @Composable
                fun Something(myMod: Modifier) {
                    Column {
                        SomethingElse(myMod)
                        SomethingElse(myMod)
                    }
                }
                @Composable
                fun FoundThisOneInTheWild(modifier: Modifier = Modifier) {
                    Box(
                        modifier = modifier
                            .size(AvatarSize.Default.size)
                            .clip(CircleShape)
                            .then(colorModifier)
                    ) {
                        Box(
                            modifier = modifier.padding(spacesBorderWidth)
                        )
                    }
                }
            """.trimIndent()

        modifierRuleAssertThat(code).hasLintViolationsWithoutAutoCorrect(
            LintViolation(
                line = 3,
                col = 5,
                detail = ComposeModifierReused.ModifierShouldBeUsedOnceOnly,
            ),
            LintViolation(
                line = 4,
                col = 9,
                detail = ComposeModifierReused.ModifierShouldBeUsedOnceOnly,
            ),
            LintViolation(
                line = 9,
                col = 5,
                detail = ComposeModifierReused.ModifierShouldBeUsedOnceOnly,
            ),
            LintViolation(
                line = 11,
                col = 9,
                detail = ComposeModifierReused.ModifierShouldBeUsedOnceOnly,
            ),
            LintViolation(
                line = 16,
                col = 5,
                detail = ComposeModifierReused.ModifierShouldBeUsedOnceOnly,
            ),
            LintViolation(
                line = 19,
                col = 5,
                detail = ComposeModifierReused.ModifierShouldBeUsedOnceOnly,
            ),
            LintViolation(
                line = 20,
                col = 5,
                detail = ComposeModifierReused.ModifierShouldBeUsedOnceOnly,
            ),
            LintViolation(
                line = 25,
                col = 9,
                detail = ComposeModifierReused.ModifierShouldBeUsedOnceOnly,
            ),
            LintViolation(
                line = 26,
                col = 9,
                detail = ComposeModifierReused.ModifierShouldBeUsedOnceOnly,
            ),
            LintViolation(
                line = 31,
                col = 5,
                detail = ComposeModifierReused.ModifierShouldBeUsedOnceOnly,
            ),
            LintViolation(
                line = 37,
                col = 9,
                detail = ComposeModifierReused.ModifierShouldBeUsedOnceOnly,
            ),
        )
    }

    @Test
    fun `errors when the modifier parameter of a Composable is tweaked or reassigned and reused`() {
        @Language("kotlin")
        val code =
            """
                @Composable
                fun Something(modifier: Modifier) {
                    Column(modifier = modifier) {
                        ChildThatReusesModifier(modifier = modifier.fillMaxWidth())
                    }
                }
                @Composable
                fun Something(modifier: Modifier) {
                    Column(modifier = modifier) {
                        val newModifier = modifier.fillMaxWidth()
                        ChildThatReusesModifier(modifier = newModifier)
                    }
                }
                @Composable
                fun Something(modifier: Modifier) {
                    val newModifier = modifier.fillMaxWidth()
                    Column(modifier = modifier) {
                        ChildThatReusesModifier(modifier = newModifier)
                    }
                }
            """.trimIndent()
        modifierRuleAssertThat(code).hasLintViolationsWithoutAutoCorrect(
            LintViolation(
                line = 3,
                col = 5,
                detail = ComposeModifierReused.ModifierShouldBeUsedOnceOnly,
            ),
            LintViolation(
                line = 4,
                col = 9,
                detail = ComposeModifierReused.ModifierShouldBeUsedOnceOnly,
            ),
            LintViolation(
                line = 9,
                col = 5,
                detail = ComposeModifierReused.ModifierShouldBeUsedOnceOnly,
            ),
            LintViolation(
                line = 11,
                col = 9,
                detail = ComposeModifierReused.ModifierShouldBeUsedOnceOnly,
            ),
            LintViolation(
                line = 17,
                col = 5,
                detail = ComposeModifierReused.ModifierShouldBeUsedOnceOnly,
            ),
            LintViolation(
                line = 18,
                col = 9,
                detail = ComposeModifierReused.ModifierShouldBeUsedOnceOnly,
            ),
        )
    }

    @Test
    fun `errors when multiple Composables use the modifier even when it's been assigned to a new val`() {
        @Language("kotlin")
        val code =
            """
                @Composable
                fun Something(modifier: Modifier) {
                    val tweakedModifier = Modifier.then(modifier).fillMaxWidth()
                    val reassignedModifier = modifier
                    val modifier3 = Modifier.fillMaxWidth()
                    Column(modifier = modifier) {
                        OkComposable(modifier = newModifier)
                        ComposableThaReusesModifier(modifier = tweakedModifier)
                        ComposableThaReusesModifier(modifier = reassignedModifier)
                        OkComposable(modifier = modifier3)
                    }
                    InnerComposable(modifier = tweakedModifier)
                }
                @Composable
                fun Something(modifier: Modifier) {
                    Column(modifier = modifier) {
                        val tweakedModifier = Modifier.then(modifier).fillMaxWidth()
                        val reassignedModifier = modifier
                        OkComposable(modifier = newModifier)
                        ComposableThaReusesModifier(modifier = tweakedModifier)
                        ComposableThaReusesModifier(modifier = reassignedModifier)
                    }
                }
            """.trimIndent()
        modifierRuleAssertThat(code).hasLintViolationsWithoutAutoCorrect(
            LintViolation(
                line = 6,
                col = 5,
                detail = ComposeModifierReused.ModifierShouldBeUsedOnceOnly,
            ),
            LintViolation(
                line = 8,
                col = 9,
                detail = ComposeModifierReused.ModifierShouldBeUsedOnceOnly,
            ),
            LintViolation(
                line = 9,
                col = 9,
                detail = ComposeModifierReused.ModifierShouldBeUsedOnceOnly,
            ),
            LintViolation(
                line = 12,
                col = 5,
                detail = ComposeModifierReused.ModifierShouldBeUsedOnceOnly,
            ),
            LintViolation(
                line = 16,
                col = 5,
                detail = ComposeModifierReused.ModifierShouldBeUsedOnceOnly,
            ),
            LintViolation(
                line = 20,
                col = 9,
                detail = ComposeModifierReused.ModifierShouldBeUsedOnceOnly,
            ),
            LintViolation(
                line = 21,
                col = 9,
                detail = ComposeModifierReused.ModifierShouldBeUsedOnceOnly,
            ),
        )
    }

    @Test
    fun `passes when a Composable only passes its modifier parameter to the root level layout`() {
        @Language("kotlin")
        val code =
            """
                @Composable
                fun Something(modifier: Modifier) {
                    Column(modifier) {
                        InternalComposable()
                        Text("Hi")
                    }
                }
                @Composable
                fun Something(modifier: Modifier) {
                    Column(modifier) {
                        ComposableWithNewModifier(Modifier.fillMaxWidth())
                        Text("Hi", modifier = Modifier.padding12())
                    }
                }
                @Composable
                fun Something(modifier: Modifier) {
                    Column(modifier) {
                        val newModifier = Modifier.weight(1f)
                        ComposableWithNewModifier(newModifier)
                        Text("Hi")
                    }
                }
                @Composable
                fun Something(modifier: Modifier) {
                    Column(modifier) {
                        val newModifier = Modifier.weight(1f)
                        if(shouldShowSomething) {
                            ComposableWithNewModifier(newModifier)
                        } else {
                            DifferentComposableWithNewModifier(newModifier)
                        }
                    }
                }
            """.trimIndent()
        modifierRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `passes when modifiers are reused for mutually exclusive branches`() {
        @Language("kotlin")
        val code =
            """
                @Composable
                fun Something(modifier: Modifier = Modifier) {
                    if (someCondition) {
                        Case1RootLevelComposable(modifier = modifier.background(HorizonColor.Black))
                    } else {
                        Case2RootLevelComposable(modifier)
                    }
                }
            """.trimIndent()
        modifierRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `passes when used on vals with lambdas`() {
        @Language("kotlin")
        val code =
            """
                @Composable
                fun Something(modifier: Modifier) {
                    Column(modifier = modifier) {
                        TrustedFriendsMembersAppBar(
                            onBackClicked = { viewModel.processUserIntent(BackClicked) },
                            onDoneClicked = { viewModel.processUserIntent(DoneClicked) }
                        )

                        val recommendedEmptyUsersContent: @Composable ((Modifier) -> Unit)? = when {
                            !recommended.isEmpty -> null
                            searchQuery.value.isEmpty() -> { localModifier: Modifier ->
                                EmptyUsersList(
                                    title = stringResource(trustedR.string.trusted_friends_members_list_empty_title),
                                    description = stringResource(trustedR.string.trusted_friends_members_list_empty_description),
                                    modifier = localModifier
                                )
                            }
                            else -> { localModifier ->
                                EmptyUsersList(
                                    title = stringResource(trustedR.string.trusted_friends_search_empty_title),
                                    description = stringResource(
                                        trustedR.string.trusted_friends_search_empty_description,
                                        searchQuery.value
                                    ),
                                    modifier = localModifier
                                )
                            }
                        }
                    }
                }
            """.trimIndent()
        modifierRuleAssertThat(code).hasNoLintViolations()
    }
}
