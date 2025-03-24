package com.josh25.vorsprungone.presentation.GraphicsUiOpenGL


import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.josh25.vorsprungone.presentation.viewmodel.MissionControlViewModel
import javax.microedition.khronos.opengles.GL10

class SceneRenderer(private val viewModel: MissionControlViewModel) : GLSurfaceView.Renderer {
    private val projectionMatrix = FloatArray(16) // Projection matrix
    private val viewMatrix = FloatArray(16)  // Camera view matrix
    private val modelMatrix = FloatArray(16) // For general transformations
    private val mvpMatrix = FloatArray(16) // Final MVP matrix

    private lateinit var terrain: TerrainGraphics
    private lateinit var rover: RoverGraphics

    var rotationX = 0f
    var rotationY = 0f
    var scale = 1f  // Zoom factor
    var gridSize = 7

    // Matrices for terrain and rover
    private val terrainModelMatrix = FloatArray(16)  // Terrain's transformation matrix
    private val roverModelMatrix = FloatArray(16)    // Rover's transformation matrix
    private val finalMvpMatrix = FloatArray(16)      // Final MVP matrix for rendering

    override fun onSurfaceCreated(gl: GL10?, p1: javax.microedition.khronos.egl.EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f) // Black background
        GLES20.glEnable(GLES20.GL_DEPTH_TEST) // Enable depth testing

        terrain = TerrainGraphics(gridSize, 11)
        rover = RoverGraphics()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        val ratio: Float = width.toFloat() / height.toFloat()
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 50f)  // Adjust far plane for better zoom-out
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        viewModel.run {
            // Apply zoom by adjusting the camera's view matrix
            Matrix.setLookAtM(
                viewMatrix, 0,
                0f, 5f, 15f * scale,  // Apply scale to the camera's position (zooming)
                0f, 0f, 0f,     // Look-at position
                0f, 1f, 0f              // Up direction
            )

            // Reset matrices before applying transformations
            Matrix.setIdentityM(terrainModelMatrix, 0)
            Matrix.setIdentityM(roverModelMatrix, 0)

            // Apply scene-wide transformations (rotate + scale)
            Matrix.rotateM(terrainModelMatrix, 0, rotationX, 1f, 0f, 0f)
            Matrix.rotateM(terrainModelMatrix, 0, rotationY, 0f, 1f, 0f)

            // Apply the same rotation to the rover so it stays aligned
            Matrix.rotateM(roverModelMatrix, 0, rotationX, 1f, 0f, 0f)
            Matrix.rotateM(roverModelMatrix, 0, rotationY, 0f, 1f, 0f)

            // Keep rover parallel to terrain but floating a bit above it
            Matrix.translateM(roverModelMatrix, 0, 0f, 0.5f, 0f)
            Matrix.rotateM(roverModelMatrix, 0, -90f, 1f, 0f, 0f) // Parallel to terrain
            Matrix.scaleM(roverModelMatrix, 0, 0.25f, 0.25f, 0.25f) // Make smaller

            // Compute MVP for terrain
            Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
            Matrix.multiplyMM(finalMvpMatrix, 0, mvpMatrix, 0, terrainModelMatrix, 0)
            terrain.draw(finalMvpMatrix)

            // Compute MVP for rover
            Matrix.multiplyMM(finalMvpMatrix, 0, mvpMatrix, 0, roverModelMatrix, 0)
            rover.draw(finalMvpMatrix)
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

    // Update scale factor for zoom
    fun onZoom(scaleFactor: Float) {
        viewModel.run {
            scale /= scaleFactor
            scale = scale.coerceIn(0.1f, 5f)  // Clamp zoom level
        }
    }
}
