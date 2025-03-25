package com.josh25.vorsprungone.presentation.GraphicsUiOpenGL


import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import com.josh25.vorsprungone.presentation.viewmodel.MissionControlViewModel
import javax.microedition.khronos.opengles.GL10
class SceneRenderer(private val viewModel: MissionControlViewModel) : GLSurfaceView.Renderer {
    private val projectionMatrix = FloatArray(16) // Projection matrix
    private val viewMatrix = FloatArray(16)  // Camera view matrix
    private val mvpMatrix = FloatArray(16) // Final MVP Model View Projection matrix

    private lateinit var terrainGraphics: TerrainGraphics
    private lateinit var rover: RoverGraphics
    //private lateinit var redTerrain: RedTerrainGraphics
    private lateinit var trajectoryGraphics: TrajectoryGraphics

    var gridSize = 7

    // Transformation Matrices
    private val terrainModelMatrix = FloatArray(16)
    //private val redTerrainModelMatrix = FloatArray(16)
    private val trajectoryModelMatrix = FloatArray(16)
    private val roverModelMatrix = FloatArray(16)
    private val finalMvpMatrix = FloatArray(16)      // Final MVP matrix for rendering

    override fun onSurfaceCreated(gl: GL10?, p1: javax.microedition.khronos.egl.EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f) // Black background
        GLES20.glEnable(GLES20.GL_DEPTH_TEST) // Enable depth testing

        terrainGraphics = TerrainGraphics(gridSize, 11)
        //redTerrain = RedTerrainGraphics(gridSize-2, 9)

        // Trajectory Waypoints
        val waypoints = listOf(
            Pair(1f, 2f),   // START N
            Pair(0f, 2f),   // LM, W
            Pair(0f, 1f),   // LM, S
            Pair(1f, 1f),   // LM, E
            Pair(1f, 2f),   // LM, N
            Pair(1f, 3f),   // LM, N
        )
        trajectoryGraphics = TrajectoryGraphics(waypoints)

        rover = RoverGraphics()
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
        Matrix.setLookAtM(
            viewMatrix, 0,
            0f, 5f, 15f * scale,  // Apply scale to the camera's position (zooming)
            0f, 0f, 0f,     // Look-at position
            0f, 1f, 0f              // Up direction
        )

        viewModel.roverState.value?.let { roverMission ->
            val roverX = roverMission.roverPosition.x.toFloat()
            val roverY = roverMission.roverPosition.y.toFloat()
        }

        // Reset matrices before applying transformations
        Matrix.setIdentityM(terrainModelMatrix, 0)
        //Matrix.setIdentityM(redTerrainModelMatrix, 0)
        Matrix.setIdentityM(trajectoryModelMatrix, 0)
        Matrix.setIdentityM(roverModelMatrix, 0)

        viewModel.run {
            // Apply scene-wide transformations (rotate + scale)
            Matrix.rotateM(terrainModelMatrix, 0, rotationX, 1f, 0f, 0f)
            Matrix.rotateM(terrainModelMatrix, 0, rotationY, 0f, 1f, 0f)

            // Apply the same rotation to the rover so it stays aligned
            Matrix.rotateM(roverModelMatrix, 0, rotationX, 1f, 0f, 0f)
            Matrix.rotateM(roverModelMatrix, 0, rotationY, 0f, 1f, 0f)

            // Keep rover parallel to terrain but floating a bit above it
            Matrix.translateM(roverModelMatrix, 0, 0f, 1.1f, 0f)
            Matrix.rotateM(roverModelMatrix, 0, -90f, 1f, 0f, 0f) // Parallel to terrain
            Matrix.scaleM(roverModelMatrix, 0, 0.25f, 0.4f, 0.25f) // Make smaller

            // Apply the same rotation to the red terrain so it stays aligned
            /*Matrix.rotateM(redTerrainModelMatrix, 0, rotationX, 1f, 0f, 0f)
            Matrix.rotateM(redTerrainModelMatrix, 0, rotationY, 0f, 1f, 0f)
            Matrix.translateM(redTerrainModelMatrix, 0, 0f, 0.3f, 0f) */

            Matrix.rotateM(trajectoryModelMatrix, 0, rotationX, 1f, 0f, 0f)
            Matrix.rotateM(trajectoryModelMatrix, 0, rotationY, 0f, 1f, 0f)
            Matrix.translateM(trajectoryModelMatrix, 0, 0f, 0.7f, 0f)

            // Compute MVP for terrain
            Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
            Matrix.multiplyMM(finalMvpMatrix, 0, mvpMatrix, 0, terrainModelMatrix, 0)
            terrainGraphics.draw(finalMvpMatrix)

            // Compute MVP for rover
            Matrix.multiplyMM(finalMvpMatrix, 0, mvpMatrix, 0, roverModelMatrix, 0)
            rover.draw(finalMvpMatrix)

            //Matrix.multiplyMM(finalMvpMatrix, 0, mvpMatrix, 0, redTerrainModelMatrix, 0)
            //redTerrain.draw(finalMvpMatrix)

            Matrix.multiplyMM(finalMvpMatrix, 0, mvpMatrix, 0, trajectoryModelMatrix, 0)
            trajectoryGraphics.draw(finalMvpMatrix)
        }
    }

    fun onRotate(dx: Float, dy: Float) {
        viewModel.run {
            // Limit vertical rotation (rotationX) to avoid flipping the rover
            rotationX -= dy * 0.5f
            rotationX = rotationX.coerceIn(-15f, 90f)

            // Limit horizontal rotation (rotationY)
            rotationY -= dx * 0.5f
            rotationY = (rotationY + 180f) % 360f - 180f
        }
    }

    fun onZoom(scaleFactor: Float) {
        Log.d("Zoom Debug", "Scale factor: $scaleFactor")
        viewModel.run {
            scale /= scaleFactor // Update scale in the ViewModel
            scale = scale.coerceIn(0.1f, 5f)  // Clamp zoom level
            Log.d("Zoom Debug", "Updated Scale: $scale")
        }
    }
}
