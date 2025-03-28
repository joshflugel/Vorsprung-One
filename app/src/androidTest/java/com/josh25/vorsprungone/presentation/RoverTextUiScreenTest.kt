package com.josh25.vorsprungone.presentation

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.josh25.vorsprungone.domain.model.Direction
import com.josh25.vorsprungone.domain.model.RoverMission
import com.josh25.vorsprungone.domain.model.Position
import com.josh25.vorsprungone.domain.model.Corner
import com.josh25.vorsprungone.presentation.screen.RoverTextUiScreen
import org.junit.Rule
import org.junit.Test

// Instrumentatino tests require a Device or Emulator (screen unlocked ofc)
class RoverTextUiScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun displays_initial_rover_state_correctly() {
        val fakeMission = RoverMission(
            topRightCorner = Corner(5, 5),
            roverPosition = Position(2, 3),
            roverDirection = Direction.W,
            movements = listOf()
        )

        val movements = mutableListOf<String>()

        composeTestRule.setContent {
            RoverTextUiScreen(
                roverState = fakeMission,
                movements = movements,
                isMissionRunning = false,
                onStartMission = {},
                newMission = {}
            )
        }

        composeTestRule.onNodeWithText("Current Position: (2, 3) - W")
            .assertExists()

        composeTestRule.onNodeWithText("Movement History:")
            .assertExists()
    }
}
