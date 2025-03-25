package com.josh25.vorsprungone.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.viewinterop.AndroidView
import com.josh25.vorsprungone.domain.model.RoverMission
import com.josh25.vorsprungone.domain.model.toRover
import com.josh25.vorsprungone.presentation.GraphicsUiOpenGL.SceneOpenGLSurfaceView
import com.josh25.vorsprungone.presentation.GraphicsUiOpenGL.SceneRenderer
import com.josh25.vorsprungone.presentation.viewmodel.MissionControlViewModel


@Composable
fun MissionControlScreen(viewModel: MissionControlViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val roverState by viewModel.roverState.collectAsState()
    val movements = remember { mutableStateListOf<String>() }

    LaunchedEffect(Unit) {
        viewModel.fetchRover()
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isLandscape = maxWidth > maxHeight

        Column(modifier = Modifier.fillMaxSize()) {

            val renderer = SceneRenderer(viewModel)

            if (isLandscape) {
                Row(modifier = Modifier.fillMaxSize().padding(start = 40.dp, end = 24.dp)) {
                    Column() {
                        Text(
                            "Vorsprung One Mission Control",
                            fontWeight = FontWeight.Bold,
                            color = Color.Green,
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(14.dp)
                        )
                        RoverTextUiScreen(
                            roverState = roverState,
                            movements = movements,
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        )
                    }
                    OpenGLComposeScreen(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        renderer = renderer,
                        viewModel
                    )
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(
                        "Vorsprung One Mission Control",
                        fontWeight = FontWeight.Bold,
                        color = Color.Green,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(14.dp)
                    )
                    OpenGLComposeScreen(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        renderer = renderer,
                        viewModel
                    )
                    RoverTextUiScreen(
                        roverState = roverState,
                        movements = movements,
                        modifier = Modifier.weight(1f).fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun OpenGLComposeScreen(modifier: Modifier = Modifier, renderer: SceneRenderer, viewModel: MissionControlViewModel) {
    val context = LocalContext.current

    AndroidView(
        factory = {
            val surfaceView = SceneOpenGLSurfaceView(context, viewModel)
            surfaceView
        },
        modifier = modifier
    )
}

@Composable
fun RoverTextUiScreen(
    roverState: RoverMission?,
    movements: MutableList<String>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        Text("Rover Position", style = MaterialTheme.typography.headlineSmall)

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
