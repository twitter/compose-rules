// Copyright 2022 Twitter, Inc.
// SPDX-License-Identifier: Apache-2.0
package com.twitter.compose.rules.detekt

import com.twitter.compose.rules.ComposeModifierReused
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

class ComposeModifierReusedCheckTest {

    private val rule = ComposeModifierReusedCheck(Config.empty)

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

        val errors = rule.lint(code)
        assertThat(errors)
            .hasStartSourceLocations(
                SourceLocation(3, 5),
                SourceLocation(4, 9),
                SourceLocation(9, 5),
                SourceLocation(11, 9),
                SourceLocation(16, 5),
                SourceLocation(19, 5),
                SourceLocation(20, 5),
                SourceLocation(25, 9),
                SourceLocation(26, 9),
                SourceLocation(31, 5),
                SourceLocation(37, 9),
            )
        for (error in errors) {
            assertThat(error).hasMessage(ComposeModifierReused.ModifierShouldBeUsedOnceOnly)
        }
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
        val errors = rule.lint(code)
        assertThat(errors).hasSize(6)
            .hasStartSourceLocations(
                SourceLocation(3, 5),
                SourceLocation(4, 9),
                SourceLocation(9, 5),
                SourceLocation(11, 9),
                SourceLocation(17, 5),
                SourceLocation(18, 9),
            )
        for (error in errors) {
            assertThat(error).hasMessage(ComposeModifierReused.ModifierShouldBeUsedOnceOnly)
        }
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
        val errors = rule.lint(code)
        assertThat(errors).hasSize(7)
            .hasStartSourceLocations(
                SourceLocation(6, 5),
                SourceLocation(8, 9),
                SourceLocation(9, 9),
                SourceLocation(12, 5),
                SourceLocation(16, 5),
                SourceLocation(20, 9),
                SourceLocation(21, 9),
            )
        for (error in errors) {
            assertThat(error).hasMessage(ComposeModifierReused.ModifierShouldBeUsedOnceOnly)
        }
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
        val errors = rule.lint(code)
        assertThat(errors).isEmpty()
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
        val errors = rule.lint(code)
        assertThat(errors).isEmpty()
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
        val errors = rule.lint(code)
        assertThat(errors).isEmpty()
    }
}
