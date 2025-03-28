package com.josh25.vorsprungone.domain.model

import com.josh25.vorsprungone.domain.model.Corner
import com.josh25.vorsprungone.domain.model.Direction
import com.josh25.vorsprungone.domain.model.Movements
import com.josh25.vorsprungone.domain.model.Position
import com.josh25.vorsprungone.domain.model.RoverMission
import com.josh25.vorsprungone.domain.model.computeFullTrajectory
import org.junit.Assert.assertEquals
import org.junit.Test

class ComputeFullTrajectoryTest {

    @Test
    fun `computeFullTrajectory generates correct path`() {
        val mission = RoverMission(
            topRightCorner = Corner(5, 5),
            roverPosition = Position(0, 0),
            roverDirection = Direction.E,
            movements = listOf(
                Movements.MOVE,
                Movements.MOVE,
                Movements.LEFT,
                Movements.MOVE,
                Movements.RIGHT,
                Movements.MOVE
            )
        )

        val expected = listOf(
            Pair(0f, 0f),  // Start
            Pair(1f, 0f),  // MOVE East
            Pair(2f, 0f),  // MOVE East
            Pair(2f, 0f),  // Turn Left -> N (no move)
            Pair(2f, 1f),  // MOVE North
            Pair(2f, 1f),  // Turn Right -> E (no move)
            Pair(3f, 1f)   // MOVE East
        )

        val result = mission.computeFullTrajectory()
        assertEquals(expected, result)
    }

    @Test
    fun `trajectory starts at initial position`() {
        val mission = RoverMission(
            topRightCorner = Corner(5, 5),
            roverPosition = Position(1, 1),
            roverDirection = Direction.N,
            movements = listOf()
        )

        val trajectory = mission.computeFullTrajectory()
        assertEquals(1, trajectory.size)
        assertEquals(Pair(1f, 1f), trajectory.first())
    }

    @Test
    fun `trajectory follows movement sequence`() {
        val mission = RoverMission(
            topRightCorner = Corner(5, 5),
            roverPosition = Position(1, 1),
            roverDirection = Direction.N,
            movements = listOf(
                Movements.MOVE,       // (1,2)
                Movements.RIGHT,      // facing E
                Movements.MOVE,       // (2,2)
                Movements.LEFT,       // facing N
                Movements.MOVE        // (2,3)
            )
        )

        val trajectory = mission.computeFullTrajectory()

        val expected = listOf(
            Pair(1f, 1f),
            Pair(1f, 2f),
            Pair(1f, 2f),  // Right turn: position unchanged
            Pair(2f, 2f),
            Pair(2f, 2f),  // Left turn: position unchanged
            Pair(2f, 3f)
        )

        assertEquals(expected, trajectory)
    }

    @Test
    fun `trajectory respects grid boundaries`() {
        val mission = RoverMission(
            topRightCorner = Corner(2, 2),
            roverPosition = Position(2, 2),
            roverDirection = Direction.N,
            movements = listOf(Movements.MOVE, Movements.MOVE, Movements.MOVE)
        )

        val trajectory = mission.computeFullTrajectory()
        val last = trajectory.last()
        assertEquals(Pair(2f, 2f), last) // Shouldn't exceed y=2
    }
}
