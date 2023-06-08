package com.nikmaram.presentaion.MusicsList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nikmaram.data.model.MusicFile
import com.nikmaram.presentaion.databinding.ListItemMusicBinding

class MusicListAdapter(private val onMusicFileClicked: (Int) -> Unit) :
    PagingDataAdapter<MusicFile, MusicViewHolder>(MusicFileDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemMusicBinding.inflate(inflater, parent, false)
        return MusicViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        val musicFile = getItem(position)
        if (musicFile != null) holder.bind(musicFile)
        holder.itemView.setOnClickListener { onMusicFileClicked(position) }
    }
    suspend fun deleteMusicItems(deletedMusicIds: List<Long>) {
        val allMusicFile = snapshot().items.toMutableList()
        val deletedMusicList = allMusicFile.filter { deletedMusicIds.contains(it.id) }
        allMusicFile.removeAll(deletedMusicList)
        val updatedData = PagingData.from(allMusicFile)
        submitData(updatedData)
    }

    suspend fun addMusicFiles(musicFiles: List<MusicFile>) {
        val currentList = snapshot().items.toMutableList()
        currentList.addAll(musicFiles)
        val updatedData = PagingData.from(currentList)
        submitData(updatedData)
    }
}


class MusicViewHolder(private val binding: ListItemMusicBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(musicFile: MusicFile) {
        binding.music = musicFile
        binding.executePendingBindings()
    }
}

class MusicFileDiffCallback : DiffUtil.ItemCallback<MusicFile>() {
    override fun areItemsTheSame(oldItem: MusicFile, newItem: MusicFile): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MusicFile, newItem: MusicFile): Boolean {
        return oldItem == newItem
    }
}
