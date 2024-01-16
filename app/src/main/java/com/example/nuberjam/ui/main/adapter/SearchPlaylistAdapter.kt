package com.example.nuberjam.ui.main.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.nuberjam.data.model.Playlist

//class SearchPlaylistAdapter(
//    private val onCardItemClick : (Playlist) -> Unit
//) : ListAdapter<Playlist, SearchPlaylistAdapter.ViewHolder>(DIFF_CALLBACK) {
//
//
//
//    companion object {
//        val DIFF_CALLBACK: DiffUtil.ItemCallback<Playlist> =
//            object : DiffUtil.ItemCallback<Playlist>() {
//                override fun areItemsTheSame(
//                    oldItem: Playlist, newItem: Playlist
//                ): Boolean {
//                    return oldItem.id == newItem.id
//                }
//
//                @SuppressLint("DiffUtilEquals")
//                override fun areContentsTheSame(
//                    oldItem: Playlist, newItem: Playlist
//                ): Boolean {
//                    return oldItem == newItem
//                }
//            }
//    }
//}