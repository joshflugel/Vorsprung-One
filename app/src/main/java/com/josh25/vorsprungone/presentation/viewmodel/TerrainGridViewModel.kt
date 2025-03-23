package com.josh25.vorsprungone.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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



/*
init {
    // startRandomMovement()
}
    private fun startRandomMovement() {
        viewModelScope.launch {
            while (true) {
                delay(2000) // Change movement every 2 seconds
                val action = listOf("FORWARD", "LEFT", "RIGHT").random()

                _roverFlow.update { state ->
                    val angleRad = Math.toRadians(state.rotationAngle.toDouble()).toFloat()
                    val dx = cos(angleRad) * 1f
                    val dy = sin(angleRad) * 1f

                    when (action) {
                        "FORWARD" -> state.copy(x = state.x + dx, y = state.y + dy)
                        "LEFT" -> state.copy(rotationAngle = state.rotationAngle - 90f)
                        "RIGHT" -> state.copy(rotationAngle = state.rotationAngle + 90f)
                        else -> state
                    }
                }
            }
        }
    }
 */