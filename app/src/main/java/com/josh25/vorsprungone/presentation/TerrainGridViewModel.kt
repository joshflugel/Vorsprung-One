package com.josh25.vorsprungone.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.josh25.vorsprungone.data.repository.MissionPlanRepository
import com.josh25.vorsprungone.domain.model.Rover
import com.josh25.vorsprungone.domain.model.RoverMission
import com.josh25.vorsprungone.domain.usecase.ExecuteRoverCommandsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TerrainGridViewModel @Inject constructor(
    private val getRoverCommandsUseCase: ExecuteRoverCommandsUseCase
) : ViewModel() {

    private val _roverState = MutableStateFlow<RoverMission?>(null)
    val roverState: StateFlow<RoverMission?> = _roverState

    fun fetchRover() {
        viewModelScope.launch {
            getRoverCommandsUseCase.execute().collect { updatedRover ->
                Log.d("RoverDebug", "New RoverMission received: $updatedRover")
                _roverState.value = updatedRover
            }
        }
    }
}