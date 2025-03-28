package com.josh25.vorsprungone.presentation

import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.assertTrue

@RunWith(AndroidJUnit4::class)
class MissionControlScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun missionControl_containsExpectedSubstrings() {
        val substrings = listOf("Vorsprung", "History", "Mission", "Rover")

        // Wait until all substrings are found at least once
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            substrings.all { substring ->
                composeTestRule.onAllNodes(hasText(substring, substring = true))
                    .fetchSemanticsNodes().isNotEmpty()
            }
        }

        substrings.forEach { substring ->
            val count = composeTestRule.onAllNodes(hasText(substring, substring = true))
                .fetchSemanticsNodes().size
            assertTrue("Expected to find at least one node with substring '$substring'", count > 0)
        }
    }

    @Test
    fun roverTextUiScreen_addsMovementHistoryEntry() {
        // Wait for "Movement History" label to appear
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodes(hasText("Movement History", substring = true))
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Define regex for expected movement pattern like: (3, 2) - E
        val positionRegex = Regex("""\(\d+,\s?\d+\)\s*-\s*[NESW]""")

        // Fetch *all* text nodes in the unmerged tree
        val allTextNodes = composeTestRule.onAllNodes(
            hasText("", substring = true),
            useUnmergedTree = true
        ).fetchSemanticsNodes()

        // Filter nodes that match the movement pattern
        val matchingNodes = allTextNodes.filter { node ->
            val text = node.config.getOrNull(androidx.compose.ui.semantics.SemanticsProperties.Text)?.joinToString("")
            text?.let { positionRegex.containsMatchIn(it) } ?: false
        }

        // Assert at least one movement entry is present
        assertTrue("Expected at least one movement entry like '(x, y) - D'", matchingNodes.isNotEmpty())
    }

    @Test
    fun pressingStartMission_addsToMovementHistory() {
        // Clear any previous movements by clicking "Change Map" first
        composeTestRule.onNode(
            hasText("Change Map", substring = true),
            useUnmergedTree = true
        ).performClick()

        // Wait a bit for new mission to be fetched and UI to stabilize
        composeTestRule.waitForIdle()

        composeTestRule.onNode(
            hasText("Start Mission", substring = true),
            useUnmergedTree = true
        ).performClick()

        // Wait for a new movement entry to appear
        val positionRegex = Regex("""\(\d+,\s?\d+\)\s*-\s*[NESW]""")
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodes(hasText("", substring = true), useUnmergedTree = true)
                .fetchSemanticsNodes()
                .mapNotNull { it.config.getOrNull(androidx.compose.ui.semantics.SemanticsProperties.Text)?.joinToString("") }
                .any { positionRegex.containsMatchIn(it) }
        }
    }

    @Test
    fun startMission_updatesMove.mentHistory() {
        // Wait until rover position is shown (to ensure mission is loaded)
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodes(hasText("(", substring = true)).fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNode(hasText("Start Mission", substring = true)).performClick()
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodes(hasText("-", substring = true)).fetchSemanticsNodes().size > 1
        }
        // Verify at least two movements appeared in history
        val movementNodes = composeTestRule.onAllNodes(hasText("-", substring = true)).fetchSemanticsNodes()
        assert(movementNodes.size > 1) {
            "Expected multiple movement entries after starting mission, but found ${movementNodes.size}."
        }
    }

}
