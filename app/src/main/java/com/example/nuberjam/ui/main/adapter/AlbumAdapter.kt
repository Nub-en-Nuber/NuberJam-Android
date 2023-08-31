package com.example.nuberjam.ui.main.adapter


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nuberjam.data.model.Album
import com.example.nuberjam.databinding.AlbumItemBinding

class AlbumAdapter : ListAdapter<Album, AlbumAdapter.ViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AlbumItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val albumItem = getItem(position)
        holder.bind(albumItem)
    }

    class ViewHolder(private var binding: AlbumItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(albumItem: Album) {
            binding.apply {
                Glide.with(itemView.context).load(albumItem.photo).into(ivAlbumImage)
                tvAlbumTitle.text = albumItem.name
            }
        }
    }


    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Album> = object : DiffUtil.ItemCallback<Album>() {
            override fun areItemsTheSame(
                oldItem: Album, newItem: Album
            ): Boolean {
                return oldItem.id == newItem.id
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: Album, newItem: Album
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}