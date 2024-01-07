package com.example.nuberjam.ui.main.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.nuberjam.R
import com.example.nuberjam.data.model.Playlist
import com.example.nuberjam.databinding.GridPlaylistItemBinding

class GridPlaylistAdapter (
    private val onCardItemClick : (Playlist) -> Unit
) : ListAdapter<Playlist, GridPlaylistAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            GridPlaylistItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val playlistItem = getItem(position)
        holder.bind(playlistItem)
    }

    inner class ViewHolder(private var binding: GridPlaylistItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(playlistItem: Playlist) {
            binding.apply {
                Glide.with(itemView.context)
                    .load(playlistItem.photo)
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .into(ivGridImage)
                tvLibraryTitle.text = playlistItem.name
                tvLibraryType.text = itemView.context.getString(R.string.playlist)
            }

            itemView.setOnClickListener {
                onCardItemClick(playlistItem)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Playlist> =
            object : DiffUtil.ItemCallback<Playlist>() {
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