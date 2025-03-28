package com.josh25.vorsprungone.viewmodel

import app.cash.turbine.test
import com.josh25.vorsprungone.domain.model.*
import com.josh25.vorsprungone.domain.repository.MissionRepository
import com.josh25.vorsprungone.domain.usecase.GetMissionPlanUseCase
import com.josh25.vorsprungone.domain.usecase.GetMissionSequenceUseCase
import com.josh25.vorsprungone.presentation.viewmodel.MissionControlViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MissionControlViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: MissionControlViewModel
    private val mockRepository: MissionRepository = mockk()

    private val fakeMission = RoverMission(
        topRightCorner = Corner(5, 5),
        roverPosition = Position(0, 0),
        roverDirection = Direction.N,
        movements = listOf(Movements.MOVE, Movements.MOVE)
    )

    @Before
    fun setUp() {
        // Stub repository methods
        coEvery { mockRepository.getMissionPlan() } returns (fakeMission to fakeMission.movements)
        coEvery { mockRepository.getNewMissionPlan() } returns fakeMission

        viewModel = MissionControlViewModel(
            getMissionSequenceUseCase = GetMissionSequenceUseCase(mockRepository),
            getMissionPlanUseCase = GetMissionPlanUseCase(mockRepository),
            dispatcher = testDispatcher
        )
    }

    @After
    fun tearDown() {
        // optional cleanup
    }

    @Test
    fun testMissionUpdatesRoverStateAndSplitIndex() = runTest(testDispatcher) {
        viewModel.roverState.test {
            viewModel.fetchMissionSequence()

            // Skip initial null value
            awaitItem()

            // Simulate time for 1st emission (after first MOVE)
            advanceTimeBy(250)
            val first = awaitItem()
            println("➡️ First RoverMission: $first")
            assertEquals(Position(0, 1), first?.roverPosition)

            // Simulate time for 2nd emission (after second MOVE)
            advanceTimeBy(250)
            val second = awaitItem()
            println("➡️ Second RoverMission: $second")
            assertEquals(Position(0, 2), second?.roverPosition)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
