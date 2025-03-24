package com.josh25.vorsprungone.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
class MissionControlViewModel @Inject constructor(
    private val getRoverCommandsUseCase: ExecuteRoverCommandsUseCase
) : ViewModel() {

    private val _roverState = MutableStateFlow<RoverMission?>(null)
    val roverState: StateFlow<RoverMission?> = _roverState

    var rotationX by mutableStateOf(0f)
    var rotationY by mutableStateOf(0f)
    var scale by mutableStateOf(1f)

    fun fetchRover() {
        viewModelScope.launch {
            getRoverCommandsUseCase.execute().collect { updatedRover ->
                Log.d("RoverDebug", "New RoverMission received: $updatedRover")
                _roverState.value = updatedRover
            }
        }
    }
}
