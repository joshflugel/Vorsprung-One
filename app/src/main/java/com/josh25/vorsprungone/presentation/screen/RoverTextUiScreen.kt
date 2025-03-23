package com.josh25.vorsprungone.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.josh25.vorsprungone.domain.model.toRover
import com.josh25.vorsprungone.presentation.viewmodel.TerrainGridViewModel


@Composable
fun RoverTextUiScreen(viewModel: TerrainGridViewModel = hiltViewModel()) {
    val roverState by viewModel.roverState.collectAsState()
    val movements = remember { mutableStateListOf<String>() }

    LaunchedEffect(Unit) {
        viewModel.fetchRover()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Rover Simulation", style = MaterialTheme.typography.headlineMedium)

        Text(
            text = roverState?.let {
                val rover = it.toRover()
                "Current Position: (${rover.x}, ${rover.y}) - ${rover.direction}"
            } ?: "Initializing...",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Movement History:")
        LazyColumn {
            items(movements) { movement ->
                Text(movement, style = MaterialTheme.typography.bodyMedium)
            }
        }

        LaunchedEffect(roverState) {
            roverState?.let { mission ->
                val rover = mission.toRover()
                val newPosition = "(${rover.x}, ${rover.y}) - ${rover.direction}"

                if (movements.isEmpty() || movements.last() != newPosition) {
                    movements.add(newPosition)
                }
            }
        }
    }
}