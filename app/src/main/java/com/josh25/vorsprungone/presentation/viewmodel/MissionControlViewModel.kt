package com.josh25.vorsprungone.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.josh25.vorsprungone.domain.model.RoverMission
import com.josh25.vorsprungone.domain.model.toRover
import com.josh25.vorsprungone.domain.usecase.GetMissionPlanUseCase
import com.josh25.vorsprungone.domain.usecase.GetMissionSequenceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MissionControlViewModel @Inject constructor(
    private val getMissionSequenceUseCase: GetMissionSequenceUseCase,
    private val getMissionPlanUseCase: GetMissionPlanUseCase
) : ViewModel() {

    private val _roverState = MutableStateFlow<RoverMission?>(null)
    val roverState: StateFlow<RoverMission?> = _roverState

    var rotationX by mutableStateOf(0f)
    var rotationY by mutableStateOf(0f)
    var scale by mutableStateOf(1f)

    fun fetchMissionSequence() {
        viewModelScope.launch {
            getMissionSequenceUseCase.execute().collect { updatedRover ->
                Log.d("RoverDebug", "New RoverMission received: $updatedRover")
                _roverState.value = updatedRover
            }
        }
    }

    fun fetchMissionPlan() {
        viewModelScope.launch {
            val missionPlan = getMissionPlanUseCase.execute()
            _roverState.value = missionPlan
            Log.e("joshtag", "missionPlan: ${missionPlan.toRover().maxX}, ${missionPlan.toRover().maxY}")
        }
    }
}
