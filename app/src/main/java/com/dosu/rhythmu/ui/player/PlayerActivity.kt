package com.dosu.rhythmu.ui.player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.dosu.rhythmu.R
import com.dosu.rhythmu.data.model.FilesRepository
import com.dosu.rhythmu.data.model.MusicFile
import com.dosu.rhythmu.databinding.ActivityPlayerBinding
import com.dosu.rhythmu.ui.player.custom.GradientFragment
import com.dosu.rhythmu.ui.player.custom.LinesFragment
import com.dosu.rhythmu.utils.MusicService
import com.dosu.rhythmu.utils.TouchDataWriter
import com.dosu.rhythmu.utils.TxtWriter
import com.dosu.rhythmu.utils.isMyServiceRunning

class PlayerActivity : AppCompatActivity(), ServiceConnection{
    private lateinit var binding: ActivityPlayerBinding
    private var musicService: MusicService? = null
    private lateinit var handler : Handler
    private val touchDataWriter = TouchDataWriter()
    private var musicFile: MusicFile? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        binding.playPause.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24)
        setContentView(binding.root)
        handler = Handler(Looper.getMainLooper())
        initMusic()
        initViews()
        startService()
    }

    private fun initMusic() {
        val rhythmId = intent.getStringExtra("rhythm_id")?: return
        val rhythmData = TxtWriter().getRhythmData(this, rhythmId)
        musicFile = FilesRepository().getAllFromSP(this).find { mf -> mf.sp_id == rhythmId }
        touchDataWriter.touchDataMap = rhythmData.touchDataMap.mapValues{v -> v.value.toMutableMap()}.toMutableMap()
    }

    private lateinit var touchFragment: TouchFragment
    private lateinit var gradientFragment: GradientFragment
    private lateinit var linesFragment: LinesFragment
    private fun initViews(){
        refreshViews()
        binding.back.setOnClickListener { finish() }
        binding.playPause.setOnClickListener {playPauseBtnClicked()}
        binding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBarNotMy: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    musicService?.let {
                        it.seekTo(progress * 1000)
                        val mCurrentPosition = it.currentPosition / 1000
                        binding.seekbar.progress = mCurrentPosition
                        binding.durationPlayed.text = formattedTime(mCurrentPosition)
                    }
                }
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })
        this@PlayerActivity.runOnUiThread(runnable {
            musicService?.let{
                val mCurrentPosition = it.currentPosition/1000
                binding.seekbar.progress = mCurrentPosition
                binding.durationPlayed.text = formattedTime(mCurrentPosition)
            }
            handler.postDelayed(this, 1000)
        })

        binding.tabDots.setupWithViewPager(binding.viewpagerPlayer, true)
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        touchFragment = TouchFragment(touchDataWriter)
        gradientFragment = GradientFragment(touchDataWriter)
        linesFragment = LinesFragment(touchDataWriter)
        viewPagerAdapter.addFragments(touchFragment, "")
        viewPagerAdapter.addFragments(gradientFragment, "")
        viewPagerAdapter.addFragments(linesFragment, "")
        binding.viewpagerPlayer.adapter = viewPagerAdapter
        binding.viewpagerPlayer.currentItem = 0
    }

    private fun refreshViews(){
        binding.playPause.setBackgroundResource(
            if(musicService?.isPlaying == true) R.drawable.ic_baseline_pause_24
            else R.drawable.ic_baseline_play_arrow_24)
        if(musicService!=null) {
            binding.seekbar.max = musicService!!.duration / 1000
            binding.durationTotal.text = formattedTime(musicService!!.duration / 1000)
            val mCurrentPosition = musicService!!.currentPosition/ 1000
            binding.seekbar.progress = mCurrentPosition
            binding.durationPlayed.text = formattedTime(mCurrentPosition)
        }
        binding.songName.text = musicFile?.name
        binding.songArtist.text = musicFile?.artist
    }

    private fun nextBtnClicked() {
        musicService?.nextBtnClicked()
        refreshViews()
    }

    private fun prevBtnClicked() {
        musicService?.prevBtnClicked()
        refreshViews()
    }

    private fun playPauseBtnClicked() {
        musicService?.let {
            binding.playPause.setBackgroundResource(if(it.isPlaying) R.drawable.ic_baseline_play_arrow_24
            else R.drawable.ic_baseline_pause_24)
            it.playPauseBtnClicked()
            return
        }
        if(musicService == null) {
            startService()
            binding.playPause.setBackgroundResource(R.drawable.ic_baseline_pause_24)
        }
    }

    private fun formattedTime(mCurrentPosition: Int): String {
        val seconds : String = (mCurrentPosition % 60).toString()
        val minutes : String = (mCurrentPosition / 60).toString()
        val totalOut = "$minutes:$seconds"
        val totalNew = "$minutes:0$seconds"
        return if (seconds.length == 1){
            totalNew
        } else {
            totalOut
        }
    }

    override fun onDestroy() {
        musicService?.onDestroy()
        super.onDestroy()
    }

    override fun onPause() {
        musicService?.pause()
        super.onPause()
    }

    override fun onResume() {
        musicService?.resume()
        super.onResume()
    }

    private fun startService(){
        if(!this.isMyServiceRunning()) {
            val intent = Intent(this, MusicService::class.java)
            applicationContext.bindService(intent, this, Context.BIND_AUTO_CREATE)
            startService(intent)
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        Log.e("Main Activity", "OnServiceConnected called")
        val myBinder = service as MusicService.MyBinder
        musicService = myBinder.service
        musicFile?.let { musicService?.playMedia(it) }
        touchDataWriter.musicService = musicService
        binding.playPause.setBackgroundResource(if(musicService?.isPlaying == true) R.drawable.ic_baseline_pause_24
        else R.drawable.ic_baseline_play_arrow_24)
        refreshViews()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicService = null
        touchDataWriter.musicService = null
    }

    private fun runnable(body: Runnable.(Runnable)->Unit) = object : Runnable {
        override fun run() {
            this.body(this)
        }
    }
}