package com.josh25.vorsprungone.domain.model

import com.josh25.vorsprungone.domain.model.Direction
import org.junit.Assert.assertEquals
import org.junit.Test

class DirectionTest {

    @Test
    fun `turnLeft rotates direction counterclockwise`() {
        assertEquals(Direction.W, Direction.N.turnLeft())
        assertEquals(Direction.S, Direction.W.turnLeft())
        assertEquals(Direction.E, Direction.S.turnLeft())
        assertEquals(Direction.N, Direction.E.turnLeft())
    }

    @Test
    fun `turnRight rotates direction clockwise`() {
        assertEquals(Direction.E, Direction.N.turnRight())
        assertEquals(Direction.S, Direction.E.turnRight())
        assertEquals(Direction.W, Direction.S.turnRight())
        assertEquals(Direction.N, Direction.W.turnRight())
    }
}