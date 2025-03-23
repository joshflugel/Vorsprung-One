package com.josh25.vorsprungone.presentation.GraphicsUiOpenGL


import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import javax.microedition.khronos.opengles.GL10

class RoverRenderer : GLSurfaceView.Renderer {
    private val projectionMatrix = FloatArray(16) // Projection
    private val viewMatrix = FloatArray(16)  // Camera view
    private val modelMatrix = FloatArray(16) // For general transformations
    private val mvpMatrix = FloatArray(16) // Final MVP matrix

    private lateinit var terrain: TerrainGraphics
    private lateinit var rover: RoverGraphics

    var rotationX = 0f
    var rotationY = 0f
    var scale = 1.5f // Make the camera zoomed out more (1 is default)

    override fun onSurfaceCreated(gl: GL10?, p1: javax.microedition.khronos.egl.EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f) // Black background
        GLES20.glEnable(GLES20.GL_DEPTH_TEST) // Enable depth testing

        terrain = TerrainGraphics()
        rover = RoverGraphics()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        val ratio: Float = width.toFloat() / height.toFloat()
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 50f)  // Adjust far plane for better zoom-out
    }

    private val terrainModelMatrix = FloatArray(16)  // Separate model matrix for terrain
    private val roverModelMatrix = FloatArray(16)    // Separate model matrix for rover
    private val finalMvpMatrix = FloatArray(16)      // Temporary matrix for each object

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        // Set up the camera view matrix (Zoom Out More)
        Matrix.setLookAtM(viewMatrix, 0,
            0f, 5f, 15f * scale,  // Adjust zoom scale (to fit objects)
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

    // Update the onRotate method with rotation limits
    fun onRotate(dx: Float, dy: Float) {
        // Limit vertical rotation (rotationX) to avoid flipping the rover
        rotationX -= dy * 0.5f
        rotationX = rotationX.coerceIn(-15f, 90f)  // Prevent rotation beyond 90 degrees up or down

        // Debug log to check rotationX values
        println("Updated rotationX: $rotationX")

        // Limit horizontal rotation (rotationY) to avoid a full 360-degree rotation
        rotationY -= dx * 0.5f
        rotationY = (rotationY + 180f) % 360f - 180f  // Keep rotationY within the range of -180 to 180 degrees

        // Debug log to check rotationY values
        println("Updated rotationY: $rotationY")
    }

    fun onZoom(scaleFactor: Float) {
        scale *= scaleFactor
        scale = scale.coerceIn(0.5f, 2f)
    }
}
