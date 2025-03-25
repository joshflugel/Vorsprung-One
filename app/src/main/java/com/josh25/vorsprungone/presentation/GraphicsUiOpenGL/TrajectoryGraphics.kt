package com.josh25.vorsprungone.presentation.GraphicsUiOpenGL

import android.opengl.GLES20
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer


class TrajectoryGraphics(private val pathCoordinates: List<Pair<Float, Float>>) {
    private val vertexBuffer: FloatBuffer
    private val program: Int
    private val vertices: FloatArray

    private val vertexShaderCode = """
        uniform mat4 uMVPMatrix;
        attribute vec4 vPosition;
        void main() {
            gl_Position = uMVPMatrix * vPosition;
        }
    """.trimIndent()

    private val fragmentShaderCode = """
        precision mediump float;
        void main() {
            gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0); // Red path
        }
    """.trimIndent()

    init {
        val verticesList = mutableListOf<Float>()

        // Add the path coordinates as vertices to form the red path
        for (i in 0 until pathCoordinates.size - 1) {
            val (x1, y1) = pathCoordinates[i]
            val (x2, y2) = pathCoordinates[i + 1]
            verticesList.addAll(listOf(
                x1, y1, 0f,  // Start point (x1, y1)
                x2, y2, 0f   // End point (x2, y2)
            ))
        }

        // Convert list to array
        vertices = FloatArray(verticesList.size)
        for (i in verticesList.indices) {
            vertices[i] = verticesList[i]
        }

        val bb = ByteBuffer.allocateDirect(vertices.size * 4)
        bb.order(ByteOrder.nativeOrder())
        vertexBuffer = bb.asFloatBuffer()
        vertexBuffer.put(vertices)
        vertexBuffer.position(0)

        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        program = GLES20.glCreateProgram().apply {
            GLES20.glAttachShader(this, vertexShader)
            GLES20.glAttachShader(this, fragmentShader)
            GLES20.glLinkProgram(this)
        }
    }

    fun draw(mvpMatrix: FloatArray) {
        GLES20.glUseProgram(program)

        val positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        val mvpHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")

        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)

        // Apply a rotation matrix to make the terrain parallel to the rover triangle
        val rotatedMVPMatrix = FloatArray(16)
        Matrix.setIdentityM(rotatedMVPMatrix, 0)

        // Rotate the terrain 90 degrees around the X-axis (align it with the rover's XY plane)
        Matrix.rotateM(rotatedMVPMatrix, 0, -90f, 1f, 0f, 0f)  // Rotate 90 degrees around X axis

        // Multiply the original mvpMatrix by the rotated terrain matrix to apply the rotation
        Matrix.multiplyMM(rotatedMVPMatrix, 0, mvpMatrix, 0, rotatedMVPMatrix, 0)

        // Pass the transformed MVP matrix to the shader
        GLES20.glUniformMatrix4fv(mvpHandle, 1, false, rotatedMVPMatrix, 0)

        GLES20.glDrawArrays(GLES20.GL_LINES, 0, vertices.size / 3) // Draw the path as lines

        GLES20.glDisableVertexAttribArray(positionHandle)
    }
}