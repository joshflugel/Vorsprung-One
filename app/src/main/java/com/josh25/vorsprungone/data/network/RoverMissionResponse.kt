package com.josh25.vorsprungone.data.network

import com.google.gson.annotations.SerializedName
import com.josh25.vorsprungone.domain.model.Corner
import com.josh25.vorsprungone.domain.model.Movements
import com.josh25.vorsprungone.domain.model.RoverDirection
import com.josh25.vorsprungone.domain.model.RoverMission
import com.josh25.vorsprungone.domain.model.RoverPosition

data class RoverMissionResponse(
    @SerializedName("topRightCorner") val topRightCorner: CornerResponse,
    @SerializedName("roverPosition") val roverPosition: PositionResponse,
    @SerializedName("roverDirection") val roverDirection: String,
    @SerializedName("movements") val movements: String
)

data class CornerResponse(
    @SerializedName("x") val x: Int,
    @SerializedName("y") val y: Int
)

data class PositionResponse(
    @SerializedName("x") val x: Int,
    @SerializedName("y") val y: Int
)

fun RoverMissionResponse.toDomain(): RoverMission {
    return RoverMission(
        topRightCorner = Corner(topRightCorner.x, topRightCorner.y),
        roverPosition = RoverPosition(roverPosition.x, roverPosition.y),
        roverDirection = RoverDirection.valueOf(roverDirection),
        movements = movements.map { char ->
            when (char) {
                'L' -> Movements.LEFT
                'R' -> Movements.RIGHT
                'M' -> Movements.MOVE
                else -> throw IllegalArgumentException("Unknown movement: $char")
            }
        }
    )
}
