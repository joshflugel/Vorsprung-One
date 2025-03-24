package com.josh25.vorsprungone.presentation.GraphicsUiOpenGL

import android.opengl.GLES20
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
class TerrainGraphics(private val width: Int = 10, private val length: Int = 10) {
    private val vertexBuffer: FloatBuffer
    private val program: Int

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
            gl_FragColor = vec4(0.0, 1.0, 0.0, 1.0); // Green wireframe
        }
    """.trimIndent()

    private val vertices: FloatArray

    init {
        // Calculate the offset to center the terrain around (0, 0)
        val halfWidth = width / 2f  // Half of the width for centering
        val halfLength = length / 2f  // Half of the length for centering
        val verticesList = mutableListOf<Float>()

        // Generate vertical lines (we need width + 1 lines)
        for (i in 0..width) {
            val offset = i.toFloat() - halfWidth // Shift the grid to center it around 0
            verticesList.addAll(listOf(
                offset, -halfLength, 0f,  // Vertical line start (offset, -halfLength)
                offset, halfLength, 0f    // Vertical line end (offset, halfLength)
            ))
        }

        // Generate horizontal lines (we need length + 1 lines)
        for (i in 0..length) {
            val offset = i.toFloat() - halfLength // Shift the grid to center it around 0
            verticesList.addAll(listOf(
                -halfWidth, offset, 0f,   // Horizontal line start (-halfWidth, offset)
                halfWidth, offset, 0f     // Horizontal line end (halfWidth, offset)
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

        GLES20.glDrawArrays(GLES20.GL_LINES, 0, vertices.size / 3) // Use GL_LINES to draw lines instead of GL_LINE_LOOP

        GLES20.glDisableVertexAttribArray(positionHandle)
    }
}
