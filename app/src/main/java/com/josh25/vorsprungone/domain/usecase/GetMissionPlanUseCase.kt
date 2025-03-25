package com.josh25.vorsprungone.domain.usecase

import com.josh25.vorsprungone.data.repository.MissionPlanRepository
import com.josh25.vorsprungone.domain.model.Movements
import com.josh25.vorsprungone.domain.model.RoverMission
import com.josh25.vorsprungone.domain.model.RoverMonad
import com.josh25.vorsprungone.domain.model.toRover
import com.josh25.vorsprungone.domain.model.toRoverMission
import javax.inject.Inject

class GetMissionPlanUseCase @Inject constructor(
    private val missionPlanRepository: MissionPlanRepository
) {
    suspend fun execute(): RoverMission {
        val (roverMission, movements) = missionPlanRepository.getMissionPlan()
        var monad = RoverMonad(roverMission.toRover())

        for (movement in movements) {
            monad = when (movement) {
                Movements.LEFT -> monad.flatMap { it.turnLeft() }
                Movements.RIGHT -> monad.flatMap { it.turnRight() }
                Movements.MOVE -> monad.flatMap { it.move() }
            }
        }
        return monad.get().toRoverMission()
    }
}
