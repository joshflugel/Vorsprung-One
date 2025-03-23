package com.josh25.vorsprungone.presentation.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.josh25.vorsprungone.presentation.GraphicsUiOpenGL.RoverGLSurfaceView

@Composable
fun Rover3DUiScreen() {
    val context = LocalContext.current  // Get the context

    AndroidView(
        factory = { RoverGLSurfaceView(context) },
        modifier = Modifier.fillMaxSize()
    )
}
