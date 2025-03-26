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

fun RoverMission.toRover(): Rover {
    return Rover(
        x = roverPosition.x,
        y = roverPosition.y,
        direction = roverDirection,
        maxX = topRightCorner.x,
        maxY = topRightCorner.y,
        movements = movements
    )
}

fun RoverMission.computeFullTrajectory(): List<Pair<Float, Float>> {
    val waypoints = mutableListOf<Pair<Float, Float>>()
    var monad = RoverMonad(this.toRover())
    waypoints.add(Pair(monad.get().x.toFloat(), monad.get().y.toFloat()))

    for (movement in movements) {
        monad = when (movement) {
            Movements.LEFT -> monad.flatMap { it.turnLeft() }
            Movements.RIGHT -> monad.flatMap { it.turnRight() }
            Movements.MOVE -> monad.flatMap { it.move() }
        }
        val rover = monad.get()
        waypoints.add(Pair(rover.x.toFloat(), rover.y.toFloat()))
    }

    return waypoints
}