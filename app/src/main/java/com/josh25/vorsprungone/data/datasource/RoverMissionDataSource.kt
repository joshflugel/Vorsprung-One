package com.josh25.vorsprungone.data.datasource

import com.josh25.vorsprungone.data.network.MockApi
import com.josh25.vorsprungone.data.network.RoverMissionResponse

class RoverMissionDataSource(private val api: MockApi) {
    suspend fun fetchRoverMissionData(): RoverMissionResponse {
        return api.getRoverMissionData()
    }
    suspend fun fetchNewRoverMissionData(): RoverMissionResponse {
        return api.getNewRoverMissionData()
    }
}
