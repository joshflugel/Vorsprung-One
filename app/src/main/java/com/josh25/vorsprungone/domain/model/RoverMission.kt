package com.josh25.vorsprungone.domain.model


data class RoverMission(
    val topRightCorner: Corner,
    val roverPosition: RoverPosition,
    val roverDirection: RoverDirection,
    val movements: List<Movements>
)

data class Corner(
    val x: Int,
    val y: Int
)

data class RoverPosition(
    val x: Int,
    val y: Int
)

enum class RoverDirection { N, S, E, W }
enum class Movements { LEFT, RIGHT, MOVE }