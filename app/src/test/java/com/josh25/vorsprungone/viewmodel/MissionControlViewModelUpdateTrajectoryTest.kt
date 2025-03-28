package com.josh25.vorsprungone.viewmodel

import com.josh25.vorsprungone.domain.repository.MissionRepository
import com.josh25.vorsprungone.domain.usecase.GetMissionPlanUseCase
import com.josh25.vorsprungone.domain.usecase.GetMissionSequenceUseCase
import com.josh25.vorsprungone.presentation.viewmodel.MissionControlViewModel
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MissionControlViewModelUpdateTrajectoryTest {

    private val testDispatcher: TestDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: MissionControlViewModel

    @Before
    fun setUp() {
        val mockRepo: MissionRepository = mockk(relaxed = true)
        viewModel = MissionControlViewModel(
            getMissionSequenceUseCase = GetMissionSequenceUseCase(mockRepo),
            getMissionPlanUseCase = GetMissionPlanUseCase(mockRepo),
            dispatcher = testDispatcher
        )
    }

    @Test
    fun `updateTrajectory sets segments and resets split index`() = runTest {
        val segments = listOf(
            0f to 0f,
            1f to 1f,
            2f to 2f
        )

        viewModel.updateTrajectory(segments)

        val state = viewModel.trajectoryState.value
        assertEquals(segments, state.segments)
        assertEquals(0, state.splitIndex)
    }

    @Test
    fun updateTrajectory_updatesStateAndResetsIndex() = runTest {
        val newSegments = listOf(
            0f to 0f,
            1f to 1f,
            2f to 2f
        )

        viewModel.updateTrajectory(newSegments)

        val trajectory = viewModel.trajectoryState.value
        assertEquals(newSegments, trajectory.segments)
        assertEquals(0, trajectory.splitIndex)
    }

    @Test
    fun updateTrajectory_updatesSegmentsAndResetsSplitIndex() = runTest {
        val dummyTrajectory = listOf(
            0f to 0f,
            1f to 1f,
            2f to 2f
        )

        viewModel.updateTrajectory(dummyTrajectory)

        val trajectory = viewModel.trajectoryState.value
        assertEquals(dummyTrajectory, trajectory.segments)
        assertEquals(0, trajectory.splitIndex)
    }
}
