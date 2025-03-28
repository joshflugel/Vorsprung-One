package com.josh25.vorsprungone.domain

import com.josh25.vorsprungone.domain.model.Direction
import com.josh25.vorsprungone.domain.model.Movements
import com.josh25.vorsprungone.domain.model.Position
import com.josh25.vorsprungone.domain.model.Rover

fun Rover(
    position: Position,
    direction: Direction = Direction.N,
    maxX: Int = 10,
    maxY: Int = 10,
    movements: List<Movements> = emptyList()
): Rover {
    return Rover(
        x = position.x,
        y = position.y,
        direction = direction,
        maxX = maxX,
        maxY = maxY,
        movements = movements
    )
}
