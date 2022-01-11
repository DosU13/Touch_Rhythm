package com.dosu.rhythmu.ui.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.dosu.rhythmu.R
import com.dosu.rhythmu.data.model.MusicFile
import com.dosu.rhythmu.ui.player.PlayerActivity
import com.dosu.rhythmu.ui.select_music.SelectMusicActivity

class RhythmAdapter(private val activity: MainActivity, musicFiles: ArrayList<MusicFile>) :
    RecyclerView.Adapter<RhythmAdapter.MyViewHolder>(){
    var rhythmMusicFiles : MutableList<MusicFile> = mutableListOf()
    init {
        rhythmMusicFiles = musicFiles
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(activity).inflate(R.layout.fragment_rhythm_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val musicFile = rhythmMusicFiles[position]
        holder.musicFile = musicFile
        holder.rhythmName.text = musicFile.name
        holder.rhythmArtist.text = musicFile.artist
        holder.container.setOnClickListener{
            val intent = Intent(activity, PlayerActivity::class.java)
            intent.putExtra("rhythm_id", holder.musicFile.sp_id)
            activity.startActivity(intent)
        }
        holder.menuMore.setOnClickListener { v ->
            val popupMenu = PopupMenu(activity, v)
            popupMenu.menuInflater.inflate(R.menu.popup_rhythm_more, popupMenu.menu)
            popupMenu.show()
            popupMenu.setOnMenuItemClickListener(object : MenuItem.OnMenuItemClickListener,
                PopupMenu.OnMenuItemClickListener {
                private val item = holder.musicFile
                override fun onMenuItemClick(it: MenuItem?): Boolean {
                    when(it?.itemId){
                        R.id.delete -> {
                            val ind = rhythmMusicFiles.indexOf(item)
                            rhythmMusicFiles.removeAt(ind)
                            notifyItemRemoved(ind)
                        }
                        R.id.export -> {
                            activity.saveFile(item.sp_id)
                            Toast.makeText(activity, "Saved to documents", Toast.LENGTH_SHORT).show()
                        }
                    }
                    return true
                }
            })
        }
    }

    override fun getItemCount(): Int {
        return rhythmMusicFiles.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(myFiles: java.util.ArrayList<MusicFile>) {
        rhythmMusicFiles = myFiles
        notifyDataSetChanged()
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var musicFile: MusicFile
        val container: ConstraintLayout = itemView.findViewById(R.id.rhythm_item_container)
        val rhythmName: TextView = itemView.findViewById(R.id.rhythm_name)
        val rhythmArtist: TextView = itemView.findViewById(R.id.rhythm_artist)
        val menuMore: ImageButton = itemView.findViewById(R.id.rhythm_details)
    }
}