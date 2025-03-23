package com.josh25.vorsprungone.presentation.GraphicsUiOpenGL

import android.opengl.GLES20
import android.util.Log

fun loadShader(type: Int, shaderCode: String): Int {
    val shader = GLES20.glCreateShader(type)
    GLES20.glShaderSource(shader, shaderCode)
    GLES20.glCompileShader(shader)

    val compileStatus = IntArray(1)
    GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0)
    if (compileStatus[0] == 0) {
        Log.e("Shader", "Compilation failed: " + GLES20.glGetShaderInfoLog(shader))
        GLES20.glDeleteShader(shader)
        throw RuntimeException("Shader compilation failed")
    }

    return shader
}
