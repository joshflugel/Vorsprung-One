package com.josh25.vorsprungone.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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

    val roverState by viewModel.roverState.collectAsState()
    val movements = remember { mutableStateListOf<String>() }
    val isMissionRunning by remember { derivedStateOf { viewModel.isMissionRunning } }

    var hasLoadedInitialMission by rememberSaveable { mutableStateOf(false) }
    val surfaceViewRef = remember { mutableStateOf<SceneOpenGLSurfaceView?>(null) }

    LaunchedEffect(Unit) {
        if (!hasLoadedInitialMission) {
            viewModel.fetchMissionPlan()
            hasLoadedInitialMission = true
        }
    }

    BoxWithConstraints() {
        val isLandscape = maxWidth > maxHeight
        Column(modifier = Modifier.fillMaxSize().padding(WindowInsets.systemBars.asPaddingValues())) {
            if (isLandscape) {
                Text(
                    text = "Vorsprung One - Mission Control",
                    fontWeight = FontWeight.Bold,
                    color = Color.Green,
                    style = MaterialTheme.typography.headlineSmall
                )
                Row(modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 2.dp, end = 2.dp)) {
                    Column() {

                        RoverTextUiScreen(
                            roverState = roverState,
                            movements = movements,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            isMissionRunning = isMissionRunning,
                            onStartMission = {viewModel.fetchMissionSequence()},
                            newMission = {viewModel.fetchMissionPlan()},
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    OpenGLComposeScreen(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        viewModel = viewModel,
                        surfaceViewRef = surfaceViewRef
                    )
                }
            } else {
                Column() {
                    Text(
                        text = " Vorsprung One - Mission Control",
                        fontWeight = FontWeight.Bold,
                        color = Color.Green,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    OpenGLComposeScreen(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        viewModel = viewModel,
                        surfaceViewRef = surfaceViewRef
                    )
                    RoverTextUiScreen(
                        roverState = roverState,
                        movements = movements,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        isMissionRunning = isMissionRunning,
                        onStartMission = {viewModel.fetchMissionSequence()},
                        newMission = {viewModel.fetchMissionPlan()},
                    )
                }
            }
        }
    }
}


@Composable
fun OpenGLComposeScreen(
    modifier: Modifier = Modifier,
    viewModel: MissionControlViewModel,
    surfaceViewRef: MutableState<SceneOpenGLSurfaceView?>
) {
    val context = LocalContext.current
    val roverState by viewModel.roverState.collectAsState()
    val trajectoryState by viewModel.trajectoryState.collectAsState()

    AndroidView(
        factory = {
            SceneOpenGLSurfaceView(context, viewModel).also { surfaceViewRef.value = it }
        },
        modifier = modifier
    )

    LaunchedEffect(roverState) {
        roverState?.let { mission ->
            surfaceViewRef.value?.initMissionAndRender(mission)
            surfaceViewRef.value?.requestRender()
            surfaceViewRef.value?.renderer?.submitMission(mission)
        }
    }
    LaunchedEffect(trajectoryState) {
        surfaceViewRef.value?.renderer?.updateTrajectoryState(trajectoryState)
        surfaceViewRef.value?.requestRender()
    }
}


@Composable
fun MainBarH(startMission: () -> Unit, newMission: () -> Unit,
             isMissionRunning: Boolean, movements: MutableList<String>){
    Row(
        modifier = Modifier.padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = {
                movements.clear()
                newMission() },
            enabled = !isMissionRunning,
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
            modifier = Modifier
                .height(30.dp)
        ) {
            Text(
                "Change Map",
                color = if (isMissionRunning) Color.Gray else Color.White
            )
        }
        Spacer(Modifier.width(8.dp))
        Button(
            onClick = {
                movements.clear()
                startMission() },
            enabled = !isMissionRunning,
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
            modifier = Modifier
                .height(30.dp)
        ) {
            Text(
                "Start Mission",
                color = if (isMissionRunning) Color.Gray else Color.White
            )
        }
    }
    Text("Rover Position", style = MaterialTheme.typography.headlineSmall)
}

@Composable
fun RoverTextUiScreen(
    roverState: RoverMission?,
    movements: MutableList<String>,
    modifier: Modifier = Modifier,
    isMissionRunning: Boolean,
    onStartMission: () -> Unit,
    newMission: () -> Unit
) {
    Column(modifier = modifier.padding(2.dp)) {
        MainBarH(
            onStartMission, newMission,
            isMissionRunning = isMissionRunning,
            movements = movements
        )
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
            itemsIndexed(movements) { index, movement ->
                Text(
                    text = movement,
                    color = if (index == 0) Color.Green else Color.Unspecified,
                    fontWeight = if (index == 0) FontWeight.Bold else FontWeight.Normal
                )

            }
        }

        LaunchedEffect(roverState) {
            roverState?.let { mission ->
                val rover = mission.toRover()
                val newPosition = "(${rover.x}, ${rover.y}) - ${rover.direction}"

                if (movements.isEmpty() || movements.last() != newPosition) {
                    movements.add(0,newPosition)
                }
            }
        }
    }
}
