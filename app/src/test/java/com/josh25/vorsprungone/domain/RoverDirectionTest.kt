package com.josh25.vorsprungone.domain

import com.josh25.vorsprungone.domain.model.Direction
import com.josh25.vorsprungone.domain.model.Position
import org.junit.Assert.assertEquals
import org.junit.Test

class RoverDirectionTest {

    @Test
    fun `turnLeft cycles direction counter-clockwise`() {
        assertEquals(Direction.W, Direction.N.turnLeft())
        assertEquals(Direction.S, Direction.W.turnLeft())
        assertEquals(Direction.E, Direction.S.turnLeft())
        assertEquals(Direction.N, Direction.E.turnLeft())
    }

    @Test
    fun `turnRight cycles direction clockwise`() {
        assertEquals(Direction.E, Direction.N.turnRight())
        assertEquals(Direction.S, Direction.E.turnRight())
        assertEquals(Direction.W, Direction.S.turnRight())
        assertEquals(Direction.N, Direction.W.turnRight())
    }

    @Test
    fun `rover turns left correctly`() {
        val rover = Rover(Position(0, 0), Direction.N)
        val result = rover.turnLeft()
        assertEquals(Direction.W, result.direction)
    }

    @Test
    fun `rover turns right correctly`() {
        val rover = Rover(Position(0, 0), Direction.N)
        val result = rover.turnRight()
        assertEquals(Direction.E, result.direction)
    }
}
