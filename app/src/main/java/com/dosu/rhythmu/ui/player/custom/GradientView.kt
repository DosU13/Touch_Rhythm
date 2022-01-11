package com.dosu.rhythmu.ui.player.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RadialGradient
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.annotation.RequiresApi
import com.dosu.rhythmu.utils.TouchDataListener

class GradientView : View {
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

    var cr = 1f
    var cg = 1f
    var cb = 1f
    var gr = 1f
    var gg = 1f
    var gb = 1f
    val factor = 0.1f
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

        val w = 400f
        val x = paddingLeft+contentWidth.toFloat()/2f
        val y = paddingTop+contentHeight.toFloat()/2f



        touchDataListener.getTouchData()?.get(0)?.let { coord ->
            setGoals(coord.x, coord.y)}
        val cen = Color.valueOf(gr, gg, gb).toArgb()
        cr += (gr - cr) * factor
        cg += (gg - cg) * factor
        cb += (gb - cb) * factor
        val edg = Color.valueOf(cr, cg, cb).toArgb()

        val gradient =  RadialGradient(x,y,w,
            cen, edg, android.graphics.Shader.TileMode.CLAMP)
        val p = Paint().apply { isDither = true; shader = gradient }

        canvas.drawCircle(x, y, 1000f, p)
    }

    private val circlePaint = Paint().apply { color = Color.CYAN }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun setGoals(x: Int, y:Int): Int{
        gr = (100-y).toFloat()/100f
        gg = if(x<=50) (50-x).toFloat()/50f else 0f
        gb = if(x>=50) (x-50).toFloat()/50f else 0f
        return Color.valueOf(gr,gg,gb).toArgb()
    }
}