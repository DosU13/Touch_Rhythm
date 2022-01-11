package com.dosu.rhythmu.utils

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.media.MediaMetadataRetriever
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.dosu.rhythmu.R
import java.io.*
import java.lang.StringBuilder

fun ImageView.load(cnt: Context?, uri: String){
    try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(uri)
        val art = retriever.embeddedPicture
        retriever.release()
        if (cnt != null) {
            Glide.with(cnt).asBitmap().load(art).error(R.drawable.default_art).into(this)
        }else{
            this.setImageResource(R.drawable.default_art)
        }
    }catch(_ : Exception){
        this.setImageResource(R.drawable.default_art)
    }
}

fun Activity.isMyServiceRunning(): Boolean {
    val manager : ActivityManager = this.getSystemService(android.content.Context.ACTIVITY_SERVICE) as ActivityManager
    manager.getRunningServices(Integer.MAX_VALUE)
        .forEach { service : ActivityManager.RunningServiceInfo ->
            if(this::class.java.name.equals(service.service.className)){
                return true
            }
        }
    return false
}
