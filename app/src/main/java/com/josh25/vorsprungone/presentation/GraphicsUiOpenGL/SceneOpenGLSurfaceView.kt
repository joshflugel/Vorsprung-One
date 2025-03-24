package com.josh25.vorsprungone.presentation.GraphicsUiOpenGL

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import com.josh25.vorsprungone.presentation.viewmodel.MissionControlViewModel



class SceneOpenGLSurfaceView(context: Context, private val viewmodel: MissionControlViewModel) : GLSurfaceView(context),
    GestureDetector.OnGestureListener,
    ScaleGestureDetector.OnScaleGestureListener {

    private val renderer: SceneRenderer
    private val scaleGestureDetector = ScaleGestureDetector(context, this)
    private val gestureDetector = GestureDetector(context, this)

    init {
        setEGLContextClientVersion(2)  // OpenGL ES 2.0
        renderer = SceneRenderer(viewmodel)
        setRenderer(renderer)
        renderMode = RENDERMODE_CONTINUOUSLY

        // INITIAL VIEW setup to zoom out the view and ensure everything fits
        renderer.scale = 1.0f
        renderer.rotationX = 0.0f
        renderer.rotationY = 0.0f
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(event)
        gestureDetector.onTouchEvent(event)
        return true
    }

    // Pinch to zoom
    override fun onScale(detector: ScaleGestureDetector): Boolean {
        renderer.onZoom(detector.scaleFactor)  // Apply zoom in the renderer
        return true
    }

    // Swipe to rotate
    override fun onScroll(e1: MotionEvent?, p1: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        renderer.onRotate(distanceX, distanceY)
        return true
    }

    override fun onScaleBegin(detector: ScaleGestureDetector) = true
    override fun onScaleEnd(detector: ScaleGestureDetector) {}
    override fun onShowPress(p0: MotionEvent) {}
    override fun onSingleTapUp(p0: MotionEvent): Boolean = false
    override fun onDown(p0: MotionEvent): Boolean = false
    override fun onLongPress(p0: MotionEvent) {}
    override fun onFling(e1: MotionEvent?, p1: MotionEvent, velocityX: Float, velocityY: Float): Boolean = false
}
