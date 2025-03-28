package com.josh25.vorsprungone.domain

import com.josh25.vorsprungone.domain.model.Direction
import com.josh25.vorsprungone.domain.model.Position
import org.junit.Assert.assertEquals
import org.junit.Test

class RoverTest {

    @Test
    fun `move north increases y within bounds`() {
        val rover = Rover(Position(2, 2), Direction.N, maxX = 5, maxY = 5)
        val result = rover.move()
        assertEquals(Position(2, 3), Position(result.x, result.y))
    }

    @Test
    fun `move south decreases y but not below 0`() {
        val rover = Rover(Position(0, 0), Direction.S)
        val result = rover.move()
        assertEquals(Position(0, 0), Position(result.x, result.y)) // already at min y
    }

    @Test
    fun `move east increases x within bounds`() {
        val rover = Rover(Position(3, 3), Direction.E, maxX = 4, maxY = 4)
        val result = rover.move()
        assertEquals(Position(4, 3), Position(result.x, result.y))
    }

    @Test
    fun `move west decreases x but not below 0`() {
        val rover = Rover(Position(0, 3), Direction.W)
        val result = rover.move()
        assertEquals(Position(0, 3), Position(result.x, result.y)) // already at min x
    }

    @Test
    fun `move north clamps to maxY`() {
        val rover = Rover(Position(2, 5), Direction.N, maxY = 5)
        val result = rover.move()
        assertEquals(Position(2, 5), Position(result.x, result.y)) // already at maxY
    }

    @Test
    fun `move east clamps to maxX`() {
        val rover = Rover(Position(5, 5), Direction.E, maxX = 5)
        val result = rover.move()
        assertEquals(Position(5, 5), Position(result.x, result.y)) // already at maxX
    }
}
