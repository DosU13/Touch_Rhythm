package com.dosu.rhythmu.ui.select_music

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.dosu.rhythmu.R
import com.dosu.rhythmu.data.model.MusicFile
import com.dosu.rhythmu.utils.load

class MusicsAdapter(private val activity: SelectMusicActivity, mFileVal: ArrayList<MusicFile>) :
    RecyclerView.Adapter<MusicsAdapter.MyViewHolder>(){
    companion object{
        lateinit var mFiles : ArrayList<MusicFile>
    }
    init {
        mFiles = mFileVal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(activity).inflate(R.layout.fragment_music_item, parent, false)
        return MyViewHolder(view)
    }

    private var selectedHolder: MyViewHolder? = null
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val musicFile = mFiles[position]
        holder.musicFile = musicFile
        holder.fileName.text = musicFile.name
        holder.fileArtist.text = musicFile.artist
        holder.albumArt.load(activity, musicFile.path)
        holder.selectBtn.visibility = if(selectedHolder?.musicFile == musicFile) View.VISIBLE else View.GONE
        holder.container.setOnClickListener{
            if(holder.selectBtn.visibility==View.VISIBLE){
                holder.selectBtn.visibility = View.GONE
                selectedHolder = null
                activity.musicService?.stop()
            }else{
                holder.selectBtn.visibility = View.VISIBLE
                selectedHolder?.selectBtn?.visibility = View.GONE
                selectedHolder = holder
                activity.musicService?.playMedia(holder.musicFile)
            }
        }
        holder.selectBtn.setOnClickListener{
            val intent = Intent()
            intent.putExtra("sp_id", holder.musicFile.sp_id)
            activity.setResult(Activity.RESULT_OK, intent)
            activity.finish()
        }
    }

    override fun getItemCount(): Int {
        return mFiles.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(myFiles: java.util.ArrayList<MusicFile>) {
        mFiles = myFiles
        notifyDataSetChanged()
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var musicFile: MusicFile
        val container: ConstraintLayout = itemView.findViewById(R.id.music_item_container)
        val fileName: TextView = itemView.findViewById(R.id.music_file_name)
        val fileArtist: TextView = itemView.findViewById(R.id.music_file_artist)
        val albumArt: ImageView = itemView.findViewById(R.id.music_img)
        val selectBtn: Button = itemView.findViewById(R.id.select_btn)
    }
}