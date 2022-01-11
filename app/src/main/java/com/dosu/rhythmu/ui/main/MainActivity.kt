package com.dosu.rhythmu.ui.main

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dosu.rhythmu.data.model.FilesRepository
import com.dosu.rhythmu.data.model.MusicFile
import com.dosu.rhythmu.databinding.ActivityMainBinding
import com.dosu.rhythmu.ui.select_music.SelectMusicActivity
import com.dosu.rhythmu.utils.TxtWriter
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initAll()
    }

    private fun initAll(){
        initRecyclerView()
        binding.addRhythmuBtn.setOnClickListener { addBtnClicked() }
        binding.searchBarMain.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
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
                rhythmAdapter.updateList(myFiles)
                return true
            }
        })
    }

    private lateinit var rhythmAdapter: RhythmAdapter
    private lateinit var allFiles: ArrayList<MusicFile>
    private val filesRepository = FilesRepository()
    private val txtWriter = TxtWriter()
    private fun initRecyclerView() {
        val files = filesRepository.getAllFromSP(this)
        val rhythmIds = txtWriter.getRhythmDataIds(this)
        allFiles = rhythmIds.mapNotNull { id -> files.find { (it.sp_id==id) } } as ArrayList<MusicFile>
        rhythmAdapter = RhythmAdapter(this, allFiles)
        binding.mainActivityRecyclerView.adapter = rhythmAdapter
        binding.mainActivityRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    }

    private val addProductLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == Activity.RESULT_OK){
            result.data?.run{
                val spId = getStringExtra("sp_id")
                val rhythmIds = txtWriter.getRhythmDataIds(this@MainActivity).toMutableList()
                if (spId != null) {
                    rhythmIds.add(spId)
                    txtWriter.setRhythmDataIds(this@MainActivity, rhythmIds)
                }
                val files = filesRepository.getAllFromSP(this@MainActivity)
                files.find{it.sp_id==spId}?.let {
                    if(::rhythmAdapter.isInitialized) rhythmAdapter.rhythmMusicFiles.add(it) }
                initAll()
            }
        }
    }

    private fun addBtnClicked() {
        val intent = Intent(this, SelectMusicActivity::class.java)
        addProductLauncher.launch(intent)
    }

    fun saveFile(spId: String) {
        txtWriter.saveFile(this, spId)
    }
}