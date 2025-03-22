package com.josh25.vorsprungone.domain.model

data class Rover(val x: Int, val y: Int, val direction: Direction, val maxX: Int, val maxY: Int) {
    fun move(): Rover = when (direction) {
        Direction.N -> copy(y = (y + 1).coerceAtMost(maxY))
        Direction.E  -> copy(x = (x + 1).coerceAtMost(maxX))
        Direction.S -> copy(y = (y - 1).coerceAtLeast(0))
        Direction.W  -> copy(x = (x - 1).coerceAtLeast(0))
    }

    fun turnLeft(): Rover = when (direction) {
        Direction.N -> copy(direction = Direction.W)
        Direction.W  -> copy(direction = Direction.S)
        Direction.S -> copy(direction = Direction.E)
        Direction.E  -> copy(direction = Direction.N)
    }

    fun turnRight(): Rover = when (direction) {
        Direction.N -> copy(direction = Direction.E)
        Direction.E  -> copy(direction = Direction.S)
        Direction.S -> copy(direction = Direction.W)
        Direction.W  -> copy(direction = Direction.N)
    }
}
