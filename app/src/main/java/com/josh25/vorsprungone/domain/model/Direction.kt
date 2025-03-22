package com.josh25.vorsprungone.domain.model

enum class Direction {
    N, S, E, W;

    fun turnLeft(): Direction = when (this) {
        N -> W
        W -> S
        S -> E
        E -> N
    }

    fun turnRight(): Direction = when (this) {
        N -> E
        E -> S
        S -> W
        W -> N
    }
}