package com.josh25.vorsprungone.presentation.GraphicsUiOpenGL


import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import com.josh25.vorsprungone.domain.model.Direction
import com.josh25.vorsprungone.domain.model.RoverMission
import com.josh25.vorsprungone.domain.model.TrajectoryState
import com.josh25.vorsprungone.domain.model.computeFullTrajectory
import com.josh25.vorsprungone.domain.model.toRover
import com.josh25.vorsprungone.presentation.viewmodel.MissionControlViewModel
import javax.microedition.khronos.opengles.GL10
import kotlin.math.log

data class xyPair(val x: Int, val y: Int)

class SceneRenderer(private val viewModel: MissionControlViewModel) : GLSurfaceView.Renderer {
    // 4x4 Transformation Matrices
    private val projectionMatrix = FloatArray(16) // Projection matrix
    private val viewMatrix = FloatArray(16)  // Camera view matrix
    private val mvpMatrix = FloatArray(16) // Working Model View Projection matrix

    var readyToRender = false // prevent drawing until data is ready

    private lateinit var terrainGraphics: TerrainGraphics
    private lateinit var roverGraphics: RoverGraphics

    private var trajectoryGraphics: TrajectoryGraphics? = null
    private var trajectoryState: TrajectoryState = TrajectoryState()
    private var pendingTrajectoryState: TrajectoryState? = null

    fun updateTrajectoryState(state: TrajectoryState) {
        pendingTrajectoryState = state
    }


    private var xOffset:Float = 0f
    private var yOffset:Float = 0f
    private var zoomFactor: Float = 1f
    var gridSize = xyPair(0, 0)
    private var roverX = 0f
    private var roverY = 0f

    // Objects Transformation Matrices
    private val terrainModelMatrix = FloatArray(16)
    private val trajectoryModelMatrix = FloatArray(16)
    private val roverModelMatrix = FloatArray(16)
    private val finalMvpMatrix = FloatArray(16)      // Final MVP matrix for rendering

    private var lastRenderedMission: RoverMission? = null

    private fun initializeSceneWithMission(mission: RoverMission) {
        gridSize = xyPair(mission.topRightCorner.x, mission.topRightCorner.y)
        roverX = mission.roverPosition.x.toFloat()
        roverY = mission.roverPosition.y.toFloat()
        xOffset = gridSize.x.toFloat() / 2
        yOffset = gridSize.y.toFloat() / 2

        terrainGraphics = TerrainGraphics(gridSize)
        roverGraphics = RoverGraphics()

        zoomFactor = calculateZoomFactor(gridSize)
        readyToRender = true

        val newTrajectory = mission.computeFullTrajectory()
        if (newTrajectory.size > 1) {
            viewModel.updateTrajectory(newTrajectory)
            trajectoryState = TrajectoryState(newTrajectory, 0) // internal cache
            trajectoryGraphics = TrajectoryGraphics(newTrajectory, gridSize) // correct offsets!
            trajectoryState.segments.forEachIndexed { i, (x, y) ->
            }
        }
    }

    private var missionPending: RoverMission? = null
    private var initialized = false
    fun submitMission(mission: RoverMission) {
        lastRenderedMission = mission
        missionPending = mission
        initialized = false
    }

    override fun onSurfaceCreated(gl: GL10?, p1: javax.microedition.khronos.egl.EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f) // Black background
        GLES20.glEnable(GLES20.GL_DEPTH_TEST) // Enable depth testing
        // If screen rotated, re-submit the last mission to ensure scene re-inits
        if (lastRenderedMission != null){ // && missionPending == null) {
            missionPending = lastRenderedMission
            initialized = false
        }
        if (trajectoryState.segments.size > 1 && gridSize.x > 0 && gridSize.y > 0) {
            Log.i("joshtag", "Surface recreated. Rebuilding trajectoryGraphics with correct offsets.")
            trajectoryGraphics = TrajectoryGraphics(trajectoryState.segments, gridSize)
        } else {
            Log.w("joshtag", "Surface recreated, but gridSize not ready: gridSize=($gridSize)")
        }
        Log.i("joshtag", "Surface recreated. gridSize=($gridSize), trajectory has ${trajectoryState.segments.size} segments")
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
        if (!initialized && missionPending != null) {
            initializeSceneWithMission(missionPending!!)
            roverGraphics.setDirection(missionPending!!.toRover().direction)
            missionPending = null
            initialized = true
        }
        pendingTrajectoryState?.let { state ->
            if (state.segments.isNotEmpty()) {
                trajectoryGraphics = TrajectoryGraphics(state.segments, gridSize)
                trajectoryState = state
            }
            pendingTrajectoryState = null
        }
        if (!readyToRender) return

        // Apply zoom by adjusting the camera's view matrix based on scale in ViewModel
        val scale = viewModel.scale
        val adjustedZoom = zoomFactor * scale

        Matrix.setLookAtM(
            viewMatrix, 0,
            0f, 10f, 15f * adjustedZoom,  // Apply scale to the camera's position (zooming)
            0f, 0f, 0f,     // Look-at position, zeroes = origin
            0f, 1f, 0f              // Up direction
        )

        // Reset matrices before applying transformations
        Matrix.setIdentityM(roverModelMatrix, 0)
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
            roverGraphics.draw(finalMvpMatrix)

            Matrix.multiplyMM(finalMvpMatrix, 0, mvpMatrix, 0, trajectoryModelMatrix, 0)
            updateCurrentRoverPosition(roverX, roverY)
            if (trajectoryGraphics == null && trajectoryState.value.segments.isNotEmpty()) {
                trajectoryGraphics = TrajectoryGraphics(trajectoryState.value.segments, gridSize)
            }

            trajectoryGraphics?.let {
                Matrix.multiplyMM(finalMvpMatrix, 0, mvpMatrix, 0, trajectoryModelMatrix, 0)
                it.draw(finalMvpMatrix, trajectoryState.value.splitIndex)
            }
        }
    }

    private fun updateCurrentRoverPosition(x: Float, y: Float) {
        val segments = trajectoryState.segments
        val index = segments.indexOfFirst { it == Pair(x, y) }
        if (index >= 0) {
            viewModel.updateTrajectorySplitIndex(index)
        }
    }

    private var surfaceView: GLSurfaceView? = null
    fun attachSurfaceView(view: GLSurfaceView) {
        surfaceView = view
    }
    private fun triggerRender() {
        surfaceView?.requestRender()
    }

    fun onRotate(dx: Float, dy: Float) {
        viewModel.run {
            // Limit vertical rotation (rotationX) to avoid seeing the scene from underground
            rotationX -= dy * 0.5f
            rotationX = rotationX.coerceIn(-35f, 90f)
            // Limit horizontal rotation (rotationY)
            rotationY -= dx * 0.5f
            rotationY = (rotationY + 180f) % 360f - 180f
        }
        triggerRender()
    }

    fun onZoom(scaleFactor: Float) {
        viewModel.run {
            scale /= scaleFactor // Update scale in the ViewModel
            scale = scale.coerceIn(0.1f, 14f)  // Clamp zoom level
        }
        triggerRender()
    }
}
