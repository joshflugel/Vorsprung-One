package com.josh25.vorsprungone.domain.repository

import com.josh25.vorsprungone.domain.model.Movements
import com.josh25.vorsprungone.domain.model.RoverMission

interface MissionRepository {
    suspend fun getMissionPlan(): Pair<RoverMission, List<Movements>>
    suspend fun getNewMissionPlan(): RoverMission
}
