package com.dosu.rhythmu.utils

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import com.dosu.rhythmu.data.model.MusicFile
import java.io.File

class MusicService : Service() {
    private lateinit var musicPlayer: MediaPlayer
    private var mBinder: IBinder =MyBinder()
    val duration get() = musicPlayer.duration
    val currentPosition get() = musicPlayer.currentPosition
    val isPlaying get() = musicPlayer.isPlaying
    val curPosMillis: Int
        get() {return musicPlayer.currentPosition}

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(::musicPlayer.isInitialized) musicPlayer.start()
        return START_STICKY
    }

    fun playPauseBtnClicked(){
        if (isPlaying) musicPlayer.pause()
        else musicPlayer.start()
    }

    fun pause() = musicPlayer.pause()
    fun resume() = musicPlayer.start()

    fun nextBtnClicked(){
        musicPlayer.stop()
        musicPlayer.release()
        //fm.next()
        createMusicPlayer()
        musicPlayer.start()
    }

    fun prevBtnClicked(){
        musicPlayer.stop()
        musicPlayer.release()
        //fm.prev()
        createMusicPlayer()
        musicPlayer.start()
    }

    fun seekTo(position : Int){
        musicPlayer.seekTo(position)
    }

    private fun createMusicPlayer() {
        //musicPlayer = MediaPlayer.create(this, fm.mp3Id)
    }

    var musicFile: MusicFile? = null
    fun playMedia(musicFile: MusicFile) {
        stop()
        this.musicFile = musicFile
        Log.e("HERE : ", musicFile.path)
        musicPlayer = MediaPlayer.create(baseContext, Uri.fromFile(File(musicFile.path)))
        musicPlayer.start()
        musicPlayer.isLooping = false
    }

    fun stop(){
        if (::musicPlayer.isInitialized) {
            musicPlayer.stop()
            musicPlayer.release()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(::musicPlayer.isInitialized) musicPlayer.stop()
    }

    override fun onBind(intent: Intent?): IBinder {
        return mBinder
    }

    inner class MyBinder: Binder(){
        val service : MusicService get() = this@MusicService
    }

    fun musicStartMS(): Long{
        return SystemClock.uptimeMillis() - musicPlayer.currentPosition
    }
}