package com.josh25.vorsprungone.domain.model

class RoverMonad {
}

class TurtleMonad(val rover: Rover) {
    fun flatMap(f: (Rover) -> RoverMonad): RoverMonad = f(rover)
    fun get(): Rover = rover
}
