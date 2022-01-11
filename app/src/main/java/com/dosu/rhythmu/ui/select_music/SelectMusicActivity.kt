package com.dosu.rhythmu.ui.select_music

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dosu.rhythmu.databinding.ActivitySelectMusicBinding
import com.dosu.rhythmu.data.model.FilesRepository
import com.dosu.rhythmu.data.model.MusicFile
import com.dosu.rhythmu.utils.MusicService
import java.util.*
import kotlin.collections.ArrayList

class SelectMusicActivity : AppCompatActivity(), ServiceConnection {
    private lateinit var binding: ActivitySelectMusicBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectMusicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        permission()
    }

    private fun permission(){
        when {
            ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED -> {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE)
            }
            ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED -> {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_CODE
                )
            }
            else -> {
                initAll()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permission()
    }

    private fun initAll(){
        initRecyclerView()
        binding.back.setOnClickListener { finish() }
        binding.selectMusicSearchBar.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val userInput = newText?.lowercase(Locale.ROOT)
                val myFiles = ArrayList<MusicFile>()
                for (song: MusicFile in allFiles) {
                    if (song.name.lowercase(Locale.ROOT).contains(userInput.toString()) ||
                        song.artist.lowercase(Locale.ROOT).contains(userInput.toString()) ||
                        song.album.lowercase(Locale.ROOT).contains(userInput.toString())
                    ) {
                        myFiles.add(song)
                    }
                }
                musicsAdapter.updateList(myFiles)
                return true
            }
        })
    }

    private lateinit var musicsAdapter: MusicsAdapter
    private lateinit var allFiles: ArrayList<MusicFile>
    private val filesRepository = FilesRepository()
    private fun initRecyclerView() {
        allFiles = filesRepository.getAllFromSP(this)
        if (allFiles.size > 0) {
            musicsAdapter = MusicsAdapter(this, allFiles)
            binding.selectMusicRecyclerView.adapter = musicsAdapter
            binding.selectMusicRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        }
    }

    companion object{
        const val REQUEST_CODE = 1
    }

    var musicService: MusicService? = null
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val myBinder = service as MusicService.MyBinder
        musicService = myBinder.service
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicService = null
    }

    override fun onDestroy() {
        musicService?.onDestroy()
        super.onDestroy()
    }

    override fun onPause() {
        applicationContext.unbindService(this)
        musicService?.pause()
        super.onPause()
    }

    override fun onResume() {
        val intent = Intent(this, MusicService::class.java)
        applicationContext.bindService(intent, this, Context.BIND_AUTO_CREATE)
        musicService?.resume()
        super.onResume()
    }
}

