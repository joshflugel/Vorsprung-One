package com.josh25.vorsprungone.presentation.GraphicsUiOpenGL

import android.opengl.GLES20
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class TrajectoryGraphics(private val pathCoordinates: List<Pair<Float, Float>>, xyPair: xyPair = xyPair(10, 10)) {
    private val xOffset: Float = xyPair.x.toFloat() / 2
    private val yOffset: Float = xyPair.y.toFloat() / 2

    private val vertexShaderCode = """
        uniform mat4 uMVPMatrix;
        attribute vec4 vPosition;
        void main() {
            gl_Position = uMVPMatrix * vPosition;
        }
    """.trimIndent()

    private val fragmentShaderCode = """
        precision mediump float;
        uniform vec4 uColor;
        void main() {
            gl_FragColor = uColor;
        }
    """.trimIndent()

    private val program: Int = run {
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        GLES20.glCreateProgram().apply {
            GLES20.glAttachShader(this, vertexShader)
            GLES20.glAttachShader(this, fragmentShader)
            GLES20.glLinkProgram(this)
        }
    }

    private val allSegments: List<FloatArray> = buildSegmentList(pathCoordinates)
    private fun buildSegmentList(coords: List<Pair<Float, Float>>): List<FloatArray> {
        return coords.zipWithNext { (x1, y1), (x2, y2) ->
            floatArrayOf(
                x1 - xOffset, y1 - yOffset, 0f,
                x2 - xOffset, y2 - yOffset, 0f
            )
        }
    }

    fun draw(mvpMatrix: FloatArray, splitIndex: Int) {
        GLES20.glUseProgram(program)

        val positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        val mvpHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        val colorHandle = GLES20.glGetUniformLocation(program, "uColor")

        GLES20.glLineWidth(4f)

        // Apply rotation to align with rover XY plane
        val rotatedMVPMatrix = FloatArray(16)
        Matrix.setIdentityM(rotatedMVPMatrix, 0)
        Matrix.rotateM(rotatedMVPMatrix, 0, -90f, 1f, 0f, 0f)
        Matrix.multiplyMM(rotatedMVPMatrix, 0, mvpMatrix, 0, rotatedMVPMatrix, 0)

        GLES20.glEnableVertexAttribArray(positionHandle)

        // Draw traveled segments in RED
        GLES20.glUniform4f(colorHandle, 1f, 0f, 0f, 1f)
        for (i in 0 until splitIndex.coerceAtMost(allSegments.size)) {
            drawSegment(allSegments[i], positionHandle, mvpHandle, rotatedMVPMatrix)
        }

        // Draw remaining segments in LIGHT GRAY
        GLES20.glUniform4f(colorHandle, 0.7f, 0.7f, 0.7f, 1f)
        for (i in splitIndex until allSegments.size) {
            drawSegment(allSegments[i], positionHandle, mvpHandle, rotatedMVPMatrix)
        }

        GLES20.glDisableVertexAttribArray(positionHandle)
    }

    private fun drawSegment(segment: FloatArray, posHandle: Int, mvpHandle: Int, mvpMatrix: FloatArray) {
        val buffer = ByteBuffer.allocateDirect(segment.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(segment)
                position(0)
            }

        GLES20.glVertexAttribPointer(posHandle, 3, GLES20.GL_FLOAT, false, 0, buffer)
        GLES20.glUniformMatrix4fv(mvpHandle, 1, false, mvpMatrix, 0)
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, 2)
    }
}
