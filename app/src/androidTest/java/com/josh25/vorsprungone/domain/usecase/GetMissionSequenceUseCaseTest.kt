package com.josh25.vorsprungone.domain.usecase

import com.josh25.vorsprungone.domain.model.*
import com.josh25.vorsprungone.domain.repository.MissionRepository
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test


class GetMissionSequenceUseCaseTest {

    @Test
    fun `emits correct rover positions and directions`() = runTest {
        val fakeMission = RoverMission(
            topRightCorner = Corner(5, 5),
            roverPosition = Position(0, 0),
            roverDirection = Direction.N,
            movements = listOf(
                Movements.MOVE,
                Movements.RIGHT,
                Movements.MOVE,
                Movements.LEFT,
                Movements.MOVE
            )
        )

        val fakeRepository = object : MissionRepository {
            override suspend fun getMissionPlan(): Pair<RoverMission, List<Movements>> {
                return fakeMission to fakeMission.movements
            }

            override suspend fun getNewMissionPlan(): RoverMission {
                error("Not needed for this test")
            }
        }

        val useCase = GetMissionSequenceUseCase(fakeRepository)

        val result = useCase.execute().toList()

        val expected = listOf(
            RoverMission(Corner(5, 5), Position(0, 1), Direction.N, listOf()),
            RoverMission(Corner(5, 5), Position(0, 1), Direction.E, listOf()),
            RoverMission(Corner(5, 5), Position(1, 1), Direction.E, listOf()),
            RoverMission(Corner(5, 5), Position(1, 1), Direction.N, listOf()),
            RoverMission(Corner(5, 5), Position(1, 2), Direction.N, listOf())
        )

        assertEquals(expected, result)
    }
}
