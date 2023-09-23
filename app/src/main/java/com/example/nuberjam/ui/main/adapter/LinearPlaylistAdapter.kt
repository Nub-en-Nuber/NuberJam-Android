package com.example.nuberjam.ui.main.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nuberjam.R
import com.example.nuberjam.data.model.Album
import com.example.nuberjam.data.model.Playlist
import com.example.nuberjam.databinding.LinearPlaylistItemBinding

class LinearPlaylistAdapter : ListAdapter<Playlist, LinearPlaylistAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LinearPlaylistItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val playlistItem = getItem(position)
        holder.bind(playlistItem)
    }

    class ViewHolder(private var binding: LinearPlaylistItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(playlistItem: Playlist) {
            binding.apply {
                Glide.with(itemView.context).load(playlistItem.photo).into(ivPlaylistImage)
                tvPlaylistName.text = playlistItem.name
                tvPlaylistType.text = itemView.context.getString(R.string.playlist)
            }
        }
    }


    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Playlist> = object : DiffUtil.ItemCallback<Playlist>() {
            override fun areItemsTheSame(
                oldItem: Playlist, newItem: Playlist
            ): Boolean {
                return oldItem.id == newItem.id
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: Playlist, newItem: Playlist
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}