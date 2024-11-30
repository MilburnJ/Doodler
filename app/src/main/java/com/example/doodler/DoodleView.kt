package com.example.doodler

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DoodleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val path = Path()
    private val paint = Paint().apply {
        color = 0xFF000000.toInt() // Default color: black
        style = Paint.Style.STROKE
        strokeWidth = 10f
        isAntiAlias = true
        isDither = true
    }
    private var alphaValue: Int = 255 // Default to fully opaque
    private val pathHistory = mutableListOf<Pair<Path, Paint>>() // Tracks paths and paints
    private val redoStack = mutableListOf<Pair<Path, Paint>>()   // Tracks redo paths

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        pathHistory.forEach { (p, paint) ->
            canvas.drawPath(p, paint)
        }
        canvas.drawPath(path, paint) // Draw the current path
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(x, y)
                redoStack.clear() // Clear redo stack on new draw
            }
            MotionEvent.ACTION_MOVE -> {
                path.lineTo(x, y)
                invalidate() // Request to redraw the view
            }
            MotionEvent.ACTION_UP -> {
                // Save the path to history
                val savedPath = Path(path)
                val savedPaint = Paint(paint)
                pathHistory.add(savedPath to savedPaint)
                path.reset()
                invalidate()
            }
        }
        return true
    }

    fun clear() {
        pathHistory.clear()
        redoStack.clear()
        path.reset()
        invalidate()
    }

    fun setBrushSize(size: Float) {
        paint.strokeWidth = size
    }

    fun setBrushColor(color: Int) {
        paint.color = color
    }

    fun setOpacity(opacity: Float) {
        alphaValue = (opacity * 255).toInt() // Convert 0..1 to 0..255
        paint.alpha = alphaValue // Update the Paint object
        invalidate()
    }

    fun undo() {
        if (pathHistory.isNotEmpty()) {
            val lastPath = pathHistory.removeAt(pathHistory.size - 1)
            redoStack.add(lastPath)
            invalidate()
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            val lastRedo = redoStack.removeAt(redoStack.size - 1)
            pathHistory.add(lastRedo)
            invalidate()
        }
    }
}
