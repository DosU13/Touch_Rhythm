package com.dosu.rhythmu.utils

import android.content.Context
import android.os.Environment
import android.util.Log
import com.dosu.rhythmu.data.model.PointerCoord
import com.dosu.rhythmu.data.model.RhythmData
import java.io.*

class TxtWriter {
    fun getRhythmDataIds(context: Context): List<String> {
        return try{
            readFromFile(RHYTHM_FILE, context).split("\n")
        }catch (_: Exception) {
            emptyList()}
    }

    fun setRhythmDataIds(context: Context, ids: List<String>){
        var temp = ""
        ids.forEach{
            temp += it+"\n"
        }
        writeToFile(RHYTHM_FILE, temp, context)
    }

    fun getRhythmData(context: Context, id: String): RhythmData{
        try {
            val fullStr = readFromFile(id, context)
            val touchDataMap: MutableMap<Long, MutableMap<Int, PointerCoord>> = mutableMapOf()
            fullStr.split("\n").forEach {
                try {
                    val data = it.split(":").map { d -> d.toInt() }
                    val value: MutableMap<Int, PointerCoord> = mutableMapOf()
                    for (i in 0 until (data.size - 1) / 3) {
                        value[data[3 * i + 1]] = PointerCoord(data[3 * i + 2], data[3 * i + 3])
                    }
                    touchDataMap[data[0].toLong()] = value
                }catch (_:Exception){}
            }
            return RhythmData(id, touchDataMap)
        }catch (e: Exception){
            return RhythmData(id, emptyMap())
        }
    }

    fun setRhythmData(context: Context, rhythmData: RhythmData){
        val temp = StringBuilder()
        rhythmData.touchDataMap.forEach { (time, map) ->
            temp.append(time)
            map.forEach{ (touchId, coord) ->
                temp.append(":").append(touchId).append(":").append(coord.x).append(":").append(coord.y)
            }
            temp.append("\n")
        }
        writeToFile(rhythmData.sp_id, temp.toString(), context)
    }

    private fun writeToFile(fileName: String, data: String, context: Context) {
        try {
            val outputStreamWriter =
                OutputStreamWriter(context.openFileOutput("$fileName.txt", Context.MODE_PRIVATE))
            outputStreamWriter.write(data)
            outputStreamWriter.close()
        } catch (e: IOException) {
            Log.e("Exception", "File write failed: $e")
        }
    }

    private fun readFromFile(fileName: String, context: Context): String {
        var ret = ""
        try {
            val inputStream: InputStream? = context.openFileInput("$fileName.txt")
            if (inputStream != null) {
                val inputStreamReader = InputStreamReader(inputStream)
                val bufferedReader = BufferedReader(inputStreamReader)
                var receiveString: String? = ""
                val stringBuilder = StringBuilder()
                while (bufferedReader.readLine().also { receiveString = it } != null) {
                    stringBuilder.append("\n").append(receiveString)
                }
                inputStream.close()
                ret = stringBuilder.toString()
            }
        } catch (e: FileNotFoundException) {
            Log.e("login activity", "File not found: $e")
        } catch (e: IOException) {
            Log.e("login activity", "Can not read file: $e")
        }
        return ret
    }

    fun saveFile(context: Context, spId: String) {
        val data = readFromFile(spId, context)
        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val file = File(dir, "spID.txt")
        try {
            val fileWriter = FileWriter(file)
            fileWriter.append(data)
            fileWriter.flush()
            fileWriter.close()
        } catch (_:Exception) {}
    }

    companion object{
        const val RHYTHM_FILE = "rhythm_data_list"
    }
}