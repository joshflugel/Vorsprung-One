package com.josh25.vorsprungone.domain.usecase

import com.josh25.vorsprungone.domain.model.RoverMission
import com.josh25.vorsprungone.domain.repository.MissionRepository
import javax.inject.Inject

class GetMissionPlanUseCase @Inject constructor(
    private val missionPlanRepository: MissionRepository
) {
    suspend fun execute(): RoverMission {
        return missionPlanRepository.getNewMissionPlan()
    }
}
