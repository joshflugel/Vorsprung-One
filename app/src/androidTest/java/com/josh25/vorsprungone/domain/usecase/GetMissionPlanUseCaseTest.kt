package com.josh25.vorsprungone.domain.usecase

import com.josh25.vorsprungone.domain.model.*
import com.josh25.vorsprungone.domain.repository.MissionRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetMissionPlanUseCaseTest {

    @Test
    fun `returns new mission from repository`() = runTest {
        val fakeMission = RoverMission(
            topRightCorner = Corner(8, 8),
            roverPosition = Position(1, 1),
            roverDirection = Direction.S,
            movements = listOf(Movements.MOVE, Movements.LEFT, Movements.RIGHT)
        )

        val fakeRepository = object : MissionRepository {
            override suspend fun getMissionPlan(): Pair<RoverMission, List<Movements>> {
                error("Not needed for this test")
            }

            override suspend fun getNewMissionPlan(): RoverMission {
                return fakeMission
            }
        }

        val useCase = GetMissionPlanUseCase(fakeRepository)
        val result = useCase.execute()

        assertEquals(fakeMission, result)
    }
}
