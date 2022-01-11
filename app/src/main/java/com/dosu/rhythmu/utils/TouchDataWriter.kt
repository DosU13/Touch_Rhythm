package com.dosu.rhythmu.utils

import android.os.SystemClock
import com.dosu.rhythmu.data.model.PointerCoord
import com.dosu.rhythmu.data.model.RhythmData


class TouchDataWriter: TouchDataListener {
    var musicService: MusicService? = null
    var editMode = false
        set(value){
            if(value) editTouchDataMap = mutableMapOf()
            field = value
        }
    var editTouchDataMap: MutableMap<Long, MutableMap<Int,PointerCoord>> = mutableMapOf()
    var touchDataMap: MutableMap<Long, MutableMap<Int,PointerCoord>> = mutableMapOf() // key in 0.01 seconds

    override fun setTouchData(uptimeMillis: Long, pointerIndex: Int, x: Int, y: Int) {
        editTouchDataMap.getOrPut(getCentiSeconds(uptimeMillis)){ mutableMapOf() }[pointerIndex] = PointerCoord(x, y)
    }

    override fun getTouchData(): Map<Int, PointerCoord>? {
        val curCentiSec = getCentiSeconds(SystemClock.uptimeMillis())
        return touchDataMap[curCentiSec]?.toMap()
    }

    private fun getCentiSeconds(uptimeMillis: Long): Long{
        if(musicService==null) return -1
        val millis = uptimeMillis - musicService!!.musicStartMS()
        return millis/10
    }

    val txtWriter = TxtWriter()
    fun saveEdit() {
        touchDataMap.putAll(editTouchDataMap)
        editTouchDataMap = mutableMapOf()
        musicService?.musicFile?.let{
            txtWriter.setRhythmData(musicService!!.applicationContext, RhythmData(it.sp_id, touchDataMap))
        }
    }

    fun cancelEdit() {
        editTouchDataMap = mutableMapOf()
    }
}