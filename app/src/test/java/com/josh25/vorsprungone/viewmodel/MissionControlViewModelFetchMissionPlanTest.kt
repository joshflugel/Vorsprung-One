package com.josh25.vorsprungone.viewmodel

import com.josh25.vorsprungone.domain.model.*
import com.josh25.vorsprungone.domain.repository.MissionRepository
import com.josh25.vorsprungone.domain.usecase.GetMissionPlanUseCase
import com.josh25.vorsprungone.domain.usecase.GetMissionSequenceUseCase
import com.josh25.vorsprungone.presentation.viewmodel.MissionControlViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MissionControlViewModelFetchMissionPlanTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: MissionControlViewModel
    private val mockRepo: MissionRepository = mockk()

    private val sampleMission = RoverMission(
        topRightCorner = Corner(5, 5),
        roverPosition = Position(1, 2),
        roverDirection = Direction.E,
        movements = listOf(Movements.LEFT, Movements.MOVE)
    )

    @Before
    fun setup() {
        coEvery { mockRepo.getNewMissionPlan() } returns sampleMission

        viewModel = MissionControlViewModel(
            GetMissionSequenceUseCase(mockRepo),
            GetMissionPlanUseCase(mockRepo),
            testDispatcher
        )
    }

    @Test
    fun `fetchMissionPlan updates state and clears trajectory`() = runTest(testDispatcher) {
        viewModel.fetchMissionPlan()
        advanceUntilIdle()

        assertEquals(sampleMission, viewModel.roverState.value)
        assertEquals(emptyList<Pair<Float, Float>>(), viewModel.trajectoryState.value.segments)
        assertEquals(0, viewModel.trajectoryState.value.splitIndex)
    }
}
