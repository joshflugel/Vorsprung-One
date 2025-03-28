package com.josh25.vorsprungone.data.repository

import com.josh25.vorsprungone.data.datasource.RoverMissionDataSource
import com.josh25.vorsprungone.data.network.toDomain
import com.josh25.vorsprungone.domain.model.Movements
import com.josh25.vorsprungone.domain.model.RoverMission
import com.josh25.vorsprungone.domain.repository.MissionRepository

class MissionPlanRepository(private val remoteDataSource: RoverMissionDataSource):
    MissionRepository {
    override suspend fun getMissionPlan(): Pair<RoverMission, List<Movements>> {
        val domainMission = remoteDataSource.fetchRoverMissionData().toDomain()
        return domainMission to domainMission.movements
    }

    override suspend fun getNewMissionPlan(): RoverMission {
        val domainMission = remoteDataSource.fetchNewRoverMissionData().toDomain()
        return domainMission
    }
}