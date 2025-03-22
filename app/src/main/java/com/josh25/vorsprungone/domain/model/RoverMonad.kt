package com.josh25.vorsprungone.domain.model

class RoverMonad(val rover: Rover) {
    fun flatMap(f: (Rover) -> Rover): RoverMonad {
        return RoverMonad(f(rover))
    }

    fun get(): Rover = rover
}
