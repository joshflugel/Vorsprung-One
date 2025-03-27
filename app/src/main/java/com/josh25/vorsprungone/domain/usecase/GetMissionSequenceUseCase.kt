package com.josh25.vorsprungone.domain.usecase

import android.util.Log
import com.josh25.vorsprungone.data.repository.MissionPlanRepository
import com.josh25.vorsprungone.domain.model.Movements
import com.josh25.vorsprungone.domain.model.RoverMission
import com.josh25.vorsprungone.domain.model.RoverMonad
import com.josh25.vorsprungone.domain.model.toRover
import com.josh25.vorsprungone.domain.model.toRoverMission
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetMissionSequenceUseCase @Inject constructor(
    private val missionPlanRepository: MissionPlanRepository
) {
    fun execute(): Flow<RoverMission> = flow {
        val (roverMission, movements) = missionPlanRepository.getMissionPlan()

        var monad = RoverMonad(roverMission.toRover())

        for (movement in movements) {
            monad = when (movement) {
                Movements.LEFT -> monad.flatMap { it.turnLeft() }
                Movements.RIGHT -> monad.flatMap { it.turnRight() }
                Movements.MOVE -> monad.flatMap { it.move() }
            }
            Log.d("RoverDebug", "Emitting: ${monad.get().toRoverMission()}")

            delay(250)
            emit(monad.get().toRoverMission())
        }
    }
}