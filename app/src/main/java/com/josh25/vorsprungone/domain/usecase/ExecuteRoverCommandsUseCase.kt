package com.josh25.vorsprungone.domain.usecase

import com.josh25.vorsprungone.domain.model.RoverMission
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ExecuteRoverCommandsUseCase {
    fun execute(rover: RoverMission, commands: String): Flow<RoverMission> = flow {
        /*
        var monad = RoverMonad(rover)

        for (command in commands) {
            monad = when (command) {
                'L' -> monad.flatMap { it.turnLeft() }
                'R' -> monad.flatMap { it.turnRight() }
                'M' -> monad.flatMap { it.move() }
                else -> monad
            }

            delay(1000) // Simulate animation delay
            emit(monad.get()) // Emit new rover state
        }

         */
    }
}
