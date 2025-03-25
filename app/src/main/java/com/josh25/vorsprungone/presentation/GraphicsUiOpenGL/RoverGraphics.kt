package com.josh25.vorsprungone.presentation.GraphicsUiOpenGL

import android.opengl.GLES20
import android.opengl.Matrix
import android.util.Log
import com.josh25.vorsprungone.domain.model.Direction
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class RoverGraphics(direction: Direction = Direction.N) {
    private val vertexBuffer: FloatBuffer
    private val program: Int
    private var triangle:Triangles = Triangles.North

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
            gl_FragColor = vec4(0.0, 0.7, 1.0, 1.0); // Light blue triangle
        }
    """.trimIndent()


    fun setDirection(direction: Direction) {
        triangle = when (direction) {
            Direction.N -> Triangles.North
            Direction.W -> Triangles.West
            Direction.E -> Triangles.East
            Direction.S -> Triangles.South
        }

        // Update the vertex buffer with new vertices based on direction
        val bb = ByteBuffer.allocateDirect(triangle.vertices.size * 4)
        bb.order(ByteOrder.nativeOrder())
        vertexBuffer.clear()
        vertexBuffer.put(triangle.vertices)
        vertexBuffer.position(0)
    }

    init {
        val bb = ByteBuffer.allocateDirect(triangle.vertices.size * 4)
        bb.order(ByteOrder.nativeOrder())
        vertexBuffer = bb.asFloatBuffer()
        vertexBuffer.put(triangle.vertices)
        vertexBuffer.position(0)
        setDirection(direction)

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
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3)
        GLES20.glDisableVertexAttribArray(positionHandle)
    }
}


enum class Triangles(val vertices: FloatArray) {
    North(
        floatArrayOf(
            0f, 0.6f, -0.35f,
            -0.2f, 0.6f, 0.25f,
            0.2f, 0.6f, 0.25f
        )
    ),
    South(
        floatArrayOf(
            0f, 0.6f, 0.35f,
            -0.2f, 0.6f, -0.25f,
            0.2f, 0.6f, -0.25f
        )
    ),
    East(
        floatArrayOf(
            0.35f, 0.6f, 0f,
            -0.25f, 0.6f, -0.2f,
            -0.25f, 0.6f, 0.2f
        )
    ),
    West(
        floatArrayOf(
            -0.35f, 0.6f, 0f,
            0.25f, 0.6f, 0.2f,
            0.25f, 0.6f, -0.2f
        )
    )
}