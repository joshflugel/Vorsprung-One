package com.josh25.vorsprungone.presentation.GraphicsUiOpenGL


import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import com.josh25.vorsprungone.domain.model.Direction
import com.josh25.vorsprungone.presentation.viewmodel.MissionControlViewModel
import javax.microedition.khronos.opengles.GL10

data class xyPair(val x: Int, val y: Int)

class SceneRenderer(private val viewModel: MissionControlViewModel) : GLSurfaceView.Renderer {
    // 4x4 Transformation Matrices
    private val projectionMatrix = FloatArray(16) // Projection matrix
    private val viewMatrix = FloatArray(16)  // Camera view matrix
    private val mvpMatrix = FloatArray(16) // Working Model View Projection matrix

    private lateinit var terrainGraphics: TerrainGraphics
    private lateinit var roverGraphics: RoverGraphics
    private lateinit var trajectoryGraphics: TrajectoryGraphics

    private var xOffset:Float = 0f
    private var yOffset:Float = 0f
    private var zoomFactor: Float = 1f

    // Objects Transformation Matrices
    private val terrainModelMatrix = FloatArray(16)
    private val trajectoryModelMatrix = FloatArray(16)
    private val roverModelMatrix = FloatArray(16)
    private val finalMvpMatrix = FloatArray(16)      // Final MVP matrix for rendering

    override fun onSurfaceCreated(gl: GL10?, p1: javax.microedition.khronos.egl.EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f) // Black background
        GLES20.glEnable(GLES20.GL_DEPTH_TEST) // Enable depth testing

        var gridSize = xyPair(9,9)
        xOffset = gridSize.x.toFloat()/2
        yOffset = gridSize.y.toFloat()/2
        terrainGraphics = TerrainGraphics(gridSize)

        // Trajectory Waypoints
        val mockWaypoints = listOf(
            Pair(0f, 0f),
            Pair(1f, 2f),   // START N
            Pair(0f, 2f),   // LM, W
            Pair(0f, 1f),   // LM, S
            Pair(1f, 1f),   // LM, E
            Pair(1f, 2f),   // LM, N
            Pair(1f, 3f),   // LM, N
        )
        trajectoryGraphics = TrajectoryGraphics(mockWaypoints, gridSize)
        roverGraphics = RoverGraphics()
        zoomFactor = calculateZoomFactor(gridSize)
    }

    // Dynamically calculate the zoom factor based on the grid size
    private fun calculateZoomFactor(gridSize: xyPair): Float {
        return when {
            gridSize.x > 15 -> 3.0f // zoom out more for larger grids
            gridSize.x > 10 -> 2.0f // moderate zoom for medium grids
            else -> 1.3f // no zoom for small grids
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        val ratio: Float = width.toFloat() / height.toFloat()
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 50f)  // Adjust far plane for better zoom-out
    }


    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        // Apply zoom by adjusting the camera's view matrix based on scale in ViewModel
        val scale = viewModel.scale
        val adjustedZoom = zoomFactor * scale


        Matrix.setLookAtM(
            viewMatrix, 0,
            0f, 10f, 15f * adjustedZoom,  // Apply scale to the camera's position (zooming)
            0f, 0f, 0f,     // Look-at position, zeroes = origin
            0f, 1f, 0f              // Up direction
        )




        var roverX = 0f
        var roverY = 0f
        Matrix.setIdentityM(roverModelMatrix, 0)
        viewModel.roverState.value?.let { roverMission ->
            roverX = roverMission.roverPosition.x.toFloat()
            roverY = roverMission.roverPosition.y.toFloat()
           // Matrix.translateM(roverModelMatrix, 0, roverX - xOffset, 0f, roverY - yOffset)
        }

        // Reset matrices before applying transformations
        Matrix.setIdentityM(terrainModelMatrix, 0)
        Matrix.setIdentityM(trajectoryModelMatrix, 0)


        viewModel.run {
            // Apply scene-wide transformations (rotate + scale) - x z -y
            Matrix.rotateM(terrainModelMatrix, 0, rotationX, 1f, 0f, 0f)
            Matrix.rotateM(terrainModelMatrix, 0, rotationY, 0f, 1f, 0f)

            Matrix.rotateM(roverModelMatrix, 0, rotationX, 1f, 0f, 0f)
            Matrix.rotateM(roverModelMatrix, 0, rotationY, 0f, 1f, 0f)
            //Matrix.translateM(roverModelMatrix, 0, -xOffset, 0f, yOffset )
             Matrix.translateM(roverModelMatrix, 0, roverX - xOffset, 0f, yOffset - roverY)

            Matrix.rotateM(trajectoryModelMatrix, 0, rotationX, 1f, 0f, 0f)
            Matrix.rotateM(trajectoryModelMatrix, 0, rotationY, 0f, 1f, 0f)
            Matrix.translateM(trajectoryModelMatrix, 0, 0f, 0.4f, 0f)


            // Compute MVP Model View Projection matrices of objects, and draw
            Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
            Matrix.multiplyMM(finalMvpMatrix, 0, mvpMatrix, 0, terrainModelMatrix, 0)
            terrainGraphics.draw(finalMvpMatrix)

            Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
            Matrix.multiplyMM(finalMvpMatrix, 0, mvpMatrix, 0, roverModelMatrix, 0)
            //roverGraphics.setDirection(Direction.W)
            roverGraphics.draw(finalMvpMatrix)

            Matrix.multiplyMM(finalMvpMatrix, 0, mvpMatrix, 0, trajectoryModelMatrix, 0)
            trajectoryGraphics.draw(finalMvpMatrix)
        }
    }

    fun onRotate(dx: Float, dy: Float) {
        viewModel.run {
            // Limit vertical rotation (rotationX) to avoid seeing the scene from underground
            rotationX -= dy * 0.5f
            rotationX = rotationX.coerceIn(-15f, 90f)

            // Limit horizontal rotation (rotationY)
            rotationY -= dx * 0.5f
            rotationY = (rotationY + 180f) % 360f - 180f
        }
    }

    fun onZoom(scaleFactor: Float) {
        viewModel.run {
            scale /= scaleFactor // Update scale in the ViewModel
            scale = scale.coerceIn(0.1f, 6f)  // Clamp zoom level
            Log.d("Zoom Debug", "Updated Scale: $scale")
        }
    }
}
