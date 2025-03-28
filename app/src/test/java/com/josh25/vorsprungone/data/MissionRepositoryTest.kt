package com.josh25.vorsprungone.data.repository

import com.josh25.vorsprungone.data.datasource.RoverMissionDataSource
import com.josh25.vorsprungone.data.network.CornerResponse
import com.josh25.vorsprungone.data.network.PositionResponse
import com.josh25.vorsprungone.data.network.RoverMissionResponse
import com.josh25.vorsprungone.domain.model.Corner
import com.josh25.vorsprungone.domain.model.Direction
import com.josh25.vorsprungone.domain.model.Movements
import com.josh25.vorsprungone.domain.model.Position
import kotlinx.coroutines.test.runTest
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MissionRepositoryTest {

    private val remoteDataSource: RoverMissionDataSource = mockk()
    private lateinit var missionRepository: MissionPlanRepository

    @Before
    fun setup() {
        missionRepository = MissionPlanRepository(remoteDataSource)
    }

    @Test
    fun `getMissionPlan returns correct RoverMission`() = runTest {
        val mockResponse = RoverMissionResponse(
            topRightCorner = CornerResponse(5, 5),
            roverPosition = PositionResponse(1, 2),
            roverDirection = "N",
            movements = "LMLMLMLMM"
        )

        coEvery { remoteDataSource.fetchRoverMissionData() } returns mockResponse

        val (roverMission, movements) = missionRepository.getMissionPlan()

        val expectedMission = com.josh25.vorsprungone.domain.model.RoverMission(
            topRightCorner = Corner(5, 5),
            roverPosition = Position(1, 2),
            roverDirection = Direction.N,
            movements = listOf(
                Movements.LEFT,
                Movements.MOVE,
                Movements.LEFT,
                Movements.MOVE,
                Movements.LEFT,
                Movements.MOVE,
                Movements.LEFT,
                Movements.MOVE,
                Movements.MOVE
            )
        )

        assertEquals(expectedMission, roverMission)
        assertEquals(expectedMission.movements, movements)
    }

    @Test
    fun `getNewMissionPlan returns correct RoverMission`() = runTest {
        val mockResponse = RoverMissionResponse(
            topRightCorner = CornerResponse(3, 3),
            roverPosition = PositionResponse(2, 2),
            roverDirection = "E",
            movements = "MMRMMRMRRM"
        )

        coEvery { remoteDataSource.fetchNewRoverMissionData() } returns mockResponse

        val roverMission = missionRepository.getNewMissionPlan()

        val expectedMission = com.josh25.vorsprungone.domain.model.RoverMission(
            topRightCorner = Corner(3, 3),
            roverPosition = Position(2, 2),
            roverDirection = Direction.E,
            movements = listOf(
                Movements.MOVE,
                Movements.MOVE,
                Movements.RIGHT,
                Movements.MOVE,
                Movements.MOVE,
                Movements.RIGHT,
                Movements.MOVE,
                Movements.RIGHT,
                Movements.RIGHT,
                Movements.MOVE
            )
        )

        assertEquals(expectedMission, roverMission)
    }
}
