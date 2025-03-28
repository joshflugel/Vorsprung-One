package com.josh25.vorsprungone.viewmodel

import com.josh25.vorsprungone.domain.repository.MissionRepository
import com.josh25.vorsprungone.presentation.viewmodel.MissionControlViewModel
import com.josh25.vorsprungone.domain.usecase.GetMissionPlanUseCase
import com.josh25.vorsprungone.domain.usecase.GetMissionSequenceUseCase
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MissionControlViewModelSetMissionRunningTest {
    private val testDispatcher: TestDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: MissionControlViewModel

    @Before
    fun setup() {
        val mockRepo: MissionRepository = mockk(relaxed = true)
        viewModel = MissionControlViewModel(
            getMissionSequenceUseCase = GetMissionSequenceUseCase(mockRepo),
            getMissionPlanUseCase = GetMissionPlanUseCase(mockRepo),
            dispatcher = testDispatcher
        )
    }

    @Test
    fun setIsMissionRunning_togglesFlag() {
        // Initially false
        assertFalse(viewModel.isMissionRunning)

        // Set to true
        viewModel.setIsMissionRunning(true)
        assertTrue(viewModel.isMissionRunning)

        // Set back to false
        viewModel.setIsMissionRunning(false)
        assertFalse(viewModel.isMissionRunning)
    }
}
