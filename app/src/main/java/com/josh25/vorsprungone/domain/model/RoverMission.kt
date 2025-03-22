package com.josh25.vorsprungone.domain.model


data class RoverMission(
    val topRightCorner: Corner,
    val roverPosition: Position,
    val roverDirection: Direction,
    val movements: List<Movements>
)

data class Corner(
    val x: Int,
    val y: Int
)

data class Position(
    val x: Int,
    val y: Int
)

enum class Direction { N, S, E, W }
enum class Movements { LEFT, RIGHT, MOVE }