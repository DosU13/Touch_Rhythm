package com.dosu.rhythmu.ui.player.custom

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.annotation.RequiresApi
import com.dosu.rhythmu.utils.TouchDataListener

class LinesView : View {
    private var _touchDataListener: TouchDataListener? = null
    var touchDataListener get() = _touchDataListener!!
        set(value) {_touchDataListener = value}

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


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // allocations per draw cycle.
        val paddingLeft = paddingLeft
        val paddingTop = paddingTop
        val paddingRight = paddingRight
        val paddingBottom = paddingBottom

        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - paddingTop - paddingBottom

        val w = 100f

        touchDataListener.getTouchData()?.values?.forEach { coord ->
            val x = coord.x.toFloat()*contentWidth.toFloat()/100f
            val c = (100-coord.y)/100f
            val col = Color.valueOf(c, 0f, 1f).toArgb()
            val gradLeft = LinearGradient(
                x-w, 0f, x, 0f,
                Color.TRANSPARENT, col, Shader.TileMode.CLAMP
            )
            val paintLeft = Paint().apply { isDither = true; shader = gradLeft }
            val gradRight = LinearGradient(
                x, 0f, x+w, 0f,
                col, Color.TRANSPARENT, Shader.TileMode.CLAMP
            )
            val paintRight = Paint().apply { isDither = true; shader = gradRight }
            canvas.drawRect(x-w,0f,x,contentHeight.toFloat(),paintLeft)
            canvas.drawRect(x,0f,x+w,contentHeight.toFloat(),paintRight)
        }
    }
}