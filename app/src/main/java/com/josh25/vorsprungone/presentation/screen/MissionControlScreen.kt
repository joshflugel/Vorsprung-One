package com.josh25.vorsprungone.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import com.josh25.vorsprungone.presentation.viewmodel.MissionControlViewModel


@Composable
fun MissionControlScreen(viewModel: MissionControlViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val roverState by viewModel.roverState.collectAsState()
    val movements = remember { mutableStateListOf<String>() }
    LaunchedEffect(Unit) {
        viewModel.fetchMissionPlan()
    }
    BoxWithConstraints() {
        val isLandscape = maxWidth > maxHeight
        Column(modifier = Modifier.fillMaxSize()) {
            if (isLandscape) {
                Row(modifier = Modifier.fillMaxSize().padding(start = 42.dp, end = 42.dp)) {
                    Column() {
                        Text(
                            text = "Vorsprung One Mission Control",
                            fontWeight = FontWeight.Bold,
                            color = Color.Green,
                            style = MaterialTheme.typography.headlineSmall
                        )
                        RoverTextUiScreen(
                            roverState = roverState,
                            movements = movements,
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            onClick = {viewModel.fetchMissionSequence()}
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    OpenGLComposeScreen(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        viewModel
                    )
                }
            } else {
                Column() {
                   // MainBar({ viewModel.fetchMissionSequence() })
                    Text(
                        text = " Vorsprung One Mission Control",
                        fontWeight = FontWeight.Bold,
                        color = Color.Green,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    OpenGLComposeScreen(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        viewModel
                    )
                    RoverTextUiScreen(
                        roverState = roverState,
                        movements = movements,
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        onClick = {viewModel.fetchMissionSequence()}
                    )
                }
            }
        }
    }
}



@Composable
fun OpenGLComposeScreen(modifier: Modifier = Modifier, viewModel: MissionControlViewModel) {
    val context = LocalContext.current
    val roverState by viewModel.roverState.collectAsState()

    // ✅ Hold reference to surfaceView so we can access it outside factory
    var surfaceViewRef by remember { mutableStateOf<SceneOpenGLSurfaceView?>(null) }

    AndroidView(
        factory = {
            SceneOpenGLSurfaceView(context, viewModel).also { surfaceViewRef = it }
        },
        modifier = modifier
    )

    LaunchedEffect(roverState) {
        roverState?.let { mission ->
            surfaceViewRef?.initMissionAndRender(mission)
            surfaceViewRef?.requestRender()
        }
    }
}


@Composable
fun MainBarH(onClick: () -> Unit){
    Row(
        modifier = Modifier.padding(horizontal = 1.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Rover Position", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.width(24.dp))
        Button(
            onClick = {onClick() },
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
            modifier = Modifier
                .height(30.dp)
        ) {
            Text("Execute Mission", color = Color.White)
        }
    }
}

@Composable
fun RoverTextUiScreen(
    roverState: RoverMission?,
    movements: MutableList<String>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(modifier = modifier.padding(12.dp)) {
        MainBarH(onClick)
        Text(
            text = roverState?.let {
                val rover = it.toRover()
                "Current Position: (${rover.x}, ${rover.y}) - ${rover.direction}"
            } ?: "Initializing...",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
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
