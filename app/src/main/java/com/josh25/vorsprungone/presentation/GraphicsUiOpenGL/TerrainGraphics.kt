package com.josh25.vorsprungone.presentation.GraphicsUiOpenGL

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer


class TerrainGraphics(private val gridSize: Int = 10) {
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
        // Calculate half the grid size to center the grid around (0,0)
        val halfGridSize = gridSize / 2f
        val verticesList = mutableListOf<Float>()

        // Horizontal lines (within the bounds of the grid)
        for (i in -halfGridSize.toInt()..halfGridSize.toInt()) {
            val offset = i.toFloat()
            verticesList.addAll(listOf(
                -halfGridSize, 0f, offset, halfGridSize, 0f, offset // Horizontal line
            ))
        }

        // Vertical lines (within the bounds of the grid)
        for (i in -halfGridSize.toInt()..halfGridSize.toInt()) {
            val offset = i.toFloat()
            verticesList.addAll(listOf(
                offset, 0f, -halfGridSize, offset, 0f, halfGridSize // Vertical line
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

        GLES20.glUniformMatrix4fv(mvpHandle, 1, false, mvpMatrix, 0)

        GLES20.glDrawArrays(GLES20.GL_LINES, 0, vertices.size / 3) // Use GL_LINES to draw lines instead of GL_LINE_LOOP

        GLES20.glDisableVertexAttribArray(positionHandle)
    }
}
