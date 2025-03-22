package com.josh25.vorsprungone.data.repository

import com.josh25.vorsprungone.data.datasource.RoverMissionDataSource
import com.josh25.vorsprungone.data.network.toDomain
import com.josh25.vorsprungone.domain.model.Movements
import com.josh25.vorsprungone.domain.model.RoverMission

class RoverRepository(private val remoteDataSource: RoverMissionDataSource) {
    suspend fun getRover(): Pair<RoverMission, List<Movements>> {
        val domainMission = remoteDataSource.fetchRoverMissionData().toDomain()

        return domainMission to domainMission.movements
    }
}