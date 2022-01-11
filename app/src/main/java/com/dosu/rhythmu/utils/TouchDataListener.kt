package com.dosu.rhythmu.utils

import android.view.MotionEvent
import com.dosu.rhythmu.data.model.PointerCoord

interface TouchDataListener {
    fun setTouchData(uptimeMillis: Long, pointerIndex: Int, x: Int, y: Int)
    fun getTouchData(): Map<Int, PointerCoord>?
}