package com.josh25.vorsprungone.domain.usecase

import com.josh25.vorsprungone.data.repository.MissionPlanRepository
import com.josh25.vorsprungone.domain.model.RoverMission
import javax.inject.Inject

class GetMissionPlanUseCase @Inject constructor(
    private val missionPlanRepository: MissionPlanRepository
) {
    suspend fun execute(): RoverMission {
        return missionPlanRepository.getMissionPlan().first
    }
}
