package com.josh25.vorsprungone.domain.model

data class TrajectoryState(
    val segments: List<Pair<Float, Float>> = emptyList(),
    val splitIndex: Int = 0
)