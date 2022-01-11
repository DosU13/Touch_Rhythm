package com.dosu.rhythmu.data.model

data class RhythmData(
    val sp_id : String,
    val touchDataMap: Map<Long, Map<Int,PointerCoord>>
)
