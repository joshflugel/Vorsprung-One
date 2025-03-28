package com.josh25.vorsprungone.domain.model

import com.josh25.vorsprungone.domain.model.Direction
import com.josh25.vorsprungone.domain.model.Rover
import org.junit.Assert.*
import org.junit.Test

// Confirm the rover obeys directions and terrain boundaries.
class RoverTest {

    private fun createRover(x: Int, y: Int, direction: Direction = Direction.N): Rover {
        return Rover(x, y, direction, maxX = 5, maxY = 5, movements = emptyList())
    }

    @Test
    fun `move north increases Y`() {
        val rover = createRover(2, 2, Direction.N)
        val moved = rover.move()
        assertEquals(2, moved.x)
        assertEquals(3, moved.y)
    }

    @Test
    fun `move east increases X`() {
        val rover = createRover(2, 2, Direction.E)
        val moved = rover.move()
        assertEquals(3, moved.x)
        assertEquals(2, moved.y)
    }

    @Test
    fun `move south decreases Y`() {
        val rover = createRover(2, 2, Direction.S)
        val moved = rover.move()
        assertEquals(2, moved.x)
        assertEquals(1, moved.y)
    }

    @Test
    fun `move west decreases X`() {
        val rover = createRover(2, 2, Direction.W)
        val moved = rover.move()
        assertEquals(1, moved.x)
        assertEquals(2, moved.y)
    }

    @Test
    fun `move does not exceed grid boundaries`() {
        val roverN = createRover(2, 5, Direction.N).move()
        assertEquals(5, roverN.y)

        val roverS = createRover(2, 0, Direction.S).move()
        assertEquals(0, roverS.y)

        val roverE = createRover(5, 2, Direction.E).move()
        assertEquals(5, roverE.x)

        val roverW = createRover(0, 2, Direction.W).move()
        assertEquals(0, roverW.x)
    }

    @Test
    fun `turnLeft works correctly`() {
        assertEquals(Direction.W, createRover(0, 0, Direction.N).turnLeft().direction)
    }

    @Test
    fun `turnRight works correctly`() {
        assertEquals(Direction.E, createRover(0, 0, Direction.N).turnRight().direction)
    }
}
