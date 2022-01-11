package com.dosu.rhythmu.ui.player

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.dosu.rhythmu.R
import com.dosu.rhythmu.data.model.PointerCoord
import com.dosu.rhythmu.utils.TouchDataListener

class TouchRhythm : View {
    private var _touchDataListener: TouchDataListener? = null
    var editMode = false
    var touchDataListener get() = _touchDataListener!!
        set(value) {_touchDataListener = value}

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(!editMode)super.onTouchEvent(event)
        if(event == null || width == 0 || height == 0) return true
        if(editMode) {
            for (h in 0 until event.historySize) {
                val uptimeMillis = (event.getHistoricalEventTime(h))
                for (i in 0 until event.pointerCount) {
                    val pId = event.getPointerId(i)
                    val xPercent =
                        (event.getHistoricalX(i, h) / width.toDouble() * 100.0).toInt()
                    val yPercent =
                        (event.getHistoricalY(i, h) / height.toDouble() * 100.0).toInt()
                    touchDataListener.setTouchData(uptimeMillis, pId, xPercent, yPercent)
                }
            }
            val coordsMap = mutableMapOf<Int, PointerCoord>()
            val uptimeMillis = event.eventTime
            for (i in 0 until event.pointerCount) {
                val pId = event.getPointerId(i)
                val xPercent = (event.getX(i) / width.toDouble() * 100.0).toInt()
                val yPercent = (event.getY(i) / height.toDouble() * 100.0).toInt()
                touchDataListener.setTouchData(uptimeMillis, pId, xPercent, yPercent)
                coordsMap[pId] = PointerCoord(xPercent, yPercent)
            }
            coordsToBeDrawn = coordsMap
        }
        return true
    }

    private val updateRunnable = object : Runnable {
        override fun run() {
            postDelayed(this, 10)
            invalidate()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        post(updateRunnable)
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int)
            : super(context, attrs, defStyle)

    var coordsToBeDrawn: Map<Int, PointerCoord>? = null
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        val paddingLeft = paddingLeft
        val paddingTop = paddingTop
        val paddingRight = paddingRight
        val paddingBottom = paddingBottom

        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - paddingTop - paddingBottom
        if(!editMode){
            coordsToBeDrawn = touchDataListener.getTouchData()
        }
        coordsToBeDrawn?.values?.forEach{ coord ->
            canvas.drawCircle(
                paddingLeft.toFloat()+contentWidth.toFloat()*coord.x.toFloat()/100f,
                paddingTop.toFloat()+contentHeight.toFloat()*coord.y.toFloat()/100f,
                30f, circlePaint
            )
        }
        coordsToBeDrawn = null
    }

    private val circlePaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.colorWhite)}
}