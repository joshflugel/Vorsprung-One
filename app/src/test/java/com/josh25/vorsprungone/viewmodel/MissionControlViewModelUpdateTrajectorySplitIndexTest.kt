package com.josh25.vorsprungone.viewmodel

import com.josh25.vorsprungone.domain.model.TrajectoryState
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

class MissionControlViewModelUpdateTrajectorySplitIndexTest {

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
    fun `updateTrajectorySplitIndex sets the correct index`() = runTest {
        // Given: current trajectory with 3 points
        val segments = listOf(0f to 0f, 1f to 1f, 2f to 2f)
        viewModel.updateTrajectory(segments)

        // When: update the index
        viewModel.updateTrajectorySplitIndex(2)

        // Then: the value is reflected correctly
        val state = viewModel.trajectoryState.value
        assertEquals(2, state.splitIndex)
    }

    @Test
    fun updateTrajectorySplitIndex_updatesOnlyIndex() = runTest {
        val initialSegments = listOf(1f to 1f, 2f to 2f)
        viewModel.updateTrajectory(initialSegments)

        viewModel.updateTrajectorySplitIndex(1)

        val trajectory = viewModel.trajectoryState.value
        assertEquals(initialSegments, trajectory.segments)
        assertEquals(1, trajectory.splitIndex)
    }
}
