package com.dosu.rhythmu.data.model

import android.app.Activity
import android.content.Context
import android.provider.MediaStore

class FilesRepository {
    var spMusicFiles = ArrayList<MusicFile>()

    fun getAllFromSP(activity: Activity): ArrayList<MusicFile>{
        val preferences =
            activity.getSharedPreferences("SortOrder", Context.MODE_PRIVATE)
        val sortOrder = preferences.getString("sorting", "sortByDate")
        val tempAudioList = ArrayList<MusicFile>()
        var order = MediaStore.MediaColumns.DATE_ADDED + " ASC"
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        when (sortOrder) {
            "sortByName" -> order = MediaStore.MediaColumns.DISPLAY_NAME + " ASC"
            "sortByDate" -> order = MediaStore.MediaColumns.DATE_ADDED + " ASC"
            "sortBySize" -> order = MediaStore.MediaColumns.SIZE + " DESC"
        }
        val projection = arrayOf(
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DATE_ADDED
        )
        val cursor = activity.contentResolver.query(uri, projection, null, null, order)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val album = cursor.getString(0)
                val title = cursor.getString(1)
                val duration = cursor.getString(2)
                val path = cursor.getString(3)
                val artist = cursor.getString(4)
                val spId = cursor.getString(5)
                val dateAdded = cursor.getString(6)

                val musicFiles = MusicFile(path, title, artist, album, duration, spId, dateAdded)
                tempAudioList.add(musicFiles)
            }
            cursor.close()
        }
        spMusicFiles = tempAudioList
        return spMusicFiles
    }
}
