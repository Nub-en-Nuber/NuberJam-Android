package com.example.nuberjam.ui.main.adapter

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.nuberjam.data.model.Music
import com.example.nuberjam.databinding.MusicItemBinding
import com.example.nuberjam.utils.Helper.concatenateArtist
import com.example.nuberjam.utils.Helper.displayDuration

class MusicAdapter : ListAdapter<Music, MusicAdapter.ViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MusicItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val musicItem = getItem(position)
        holder.bind(musicItem)
    }

    class ViewHolder(var binding: MusicItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(musicItem: Music) {
            binding.apply {
                Glide.with(itemView.context).load(musicItem.photo).into(imvAlbum)
                tvTitle.text = musicItem.name
                tvSinger.text = musicItem.artist?.let { concatenateArtist(it) }
                tvDuration.text = musicItem.duration?.let { displayDuration(it) }
            }
        }
    }


    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Music> = object : DiffUtil.ItemCallback<Music>() {
            override fun areItemsTheSame(
                oldItem: Music, newItem: Music
            ): Boolean {
                return oldItem.id == newItem.id
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: Music, newItem: Music
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}