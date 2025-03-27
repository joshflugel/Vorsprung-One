package com.josh25.vorsprungone.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.josh25.vorsprungone.domain.model.RoverMission
import com.josh25.vorsprungone.domain.model.TrajectoryState
import com.josh25.vorsprungone.domain.model.computeFullTrajectory
import com.josh25.vorsprungone.domain.model.toRover
import com.josh25.vorsprungone.domain.usecase.GetMissionPlanUseCase
import com.josh25.vorsprungone.domain.usecase.GetMissionSequenceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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

    private val _trajectoryState = MutableStateFlow(TrajectoryState())
    val trajectoryState: StateFlow<TrajectoryState> = _trajectoryState

    var isMissionRunning by mutableStateOf(false)
        private set


    var rotationX by mutableFloatStateOf(0f)
    var rotationY by mutableFloatStateOf(0f)
    var scale by mutableFloatStateOf(1f)

    fun fetchMissionSequence() {
        viewModelScope.launch {
            setIsMissionRunning(true)
            getMissionSequenceUseCase.execute().collect { updatedRover ->
                Log.d("RoverDebug", "New RoverMission received: $updatedRover")
                _roverState.value = updatedRover
                val newPosition = Pair(
                    updatedRover.roverPosition.x.toFloat(),
                    updatedRover.roverPosition.y.toFloat()
                )
                val index = _trajectoryState.value.segments.indexOfFirst { it == newPosition }
                if (index != -1) {
                    updateTrajectorySplitIndex(index)
                }
            }
            delay(900)
            setIsMissionRunning(false)
        }
    }

    fun fetchMissionPlan() {
        viewModelScope.launch {
            val missionPlan = getMissionPlanUseCase.execute()
            _trajectoryState.value = TrajectoryState()
            _roverState.value = missionPlan
        }
    }

    fun setIsMissionRunning(running: Boolean) {
        isMissionRunning = running
    }

    fun updateTrajectorySplitIndex(index: Int) {
        _trajectoryState.value = _trajectoryState.value.copy(splitIndex = index)
    }

    fun updateTrajectory(segments: List<Pair<Float, Float>>) {
        _trajectoryState.value = TrajectoryState(segments = segments, splitIndex = 0)
    }
}
