package com.josh25.vorsprungone.presentation

import androidx.lifecycle.ViewModel
import com.josh25.vorsprungone.data.repository.MissionPlanRepository
import com.josh25.vorsprungone.domain.usecase.ExecuteRoverCommandsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TerrainGridViewModel @Inject constructor(
    private val missionPlanRepository: MissionPlanRepository,
    private val roverCommandsUseCase: ExecuteRoverCommandsUseCase
) : ViewModel() {
}