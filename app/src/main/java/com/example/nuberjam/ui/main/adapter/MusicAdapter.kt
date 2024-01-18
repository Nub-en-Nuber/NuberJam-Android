package com.example.nuberjam.ui.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.example.nuberjam.R
import com.example.nuberjam.data.model.Music
import com.example.nuberjam.databinding.FavoriteStateButtonBinding
import com.example.nuberjam.databinding.MusicItemBinding
import com.example.nuberjam.databinding.MusicKebabItemBinding
import com.example.nuberjam.ui.main.library.detail.deletemusic.DeleteMusicFromPlaylistDialogFragment
import com.example.nuberjam.ui.main.library.detail.searchplaylist.SearchPlaylistDialogFragment
import com.example.nuberjam.utils.Helper.concatenateArtist
import com.example.nuberjam.utils.Helper.displayDuration
import com.example.nuberjam.utils.LibraryDetailType

class MusicAdapter(
    private val musicAdapterCallback: MusicAdapterCallback,
    private val viewType: LibraryDetailType = LibraryDetailType.Favorite,
    private val childFragmentManager: FragmentManager
) : ListAdapter<Music, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    override fun getItemViewType(position: Int): Int {
        return viewType.code
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == LibraryDetailType.Playlist.code) {
            val binding =
                MusicKebabItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            MusicViewHolder(binding, viewType, musicAdapterCallback)
        } else {
            val binding =
                MusicItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            MusicViewHolder(binding, viewType, musicAdapterCallback)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val musicItem = getItem(position)
        (holder as MusicViewHolder<*>).bind(musicItem)
    }

    inner class MusicViewHolder<T : ViewBinding>(
        private val binding: T,
        private val viewType: Int,
        private val callback: MusicAdapterCallback
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(musicItem: Music) {
            if (viewType == LibraryDetailType.Playlist.code) {
                with(binding as MusicKebabItemBinding) {
                    Glide.with(itemView.context).load(musicItem.albumPhoto).into(imvAlbum)
                    tvTitle.text = musicItem.name
                    tvSinger.text = musicItem.artist?.let { concatenateArtist(it) }
                    tvDuration.text = musicItem.duration?.let { displayDuration(it) }

                    buttonFavoriteState.imbLove.setImageResource(
                        if (musicItem.isFavorite == true) R.drawable.ic_love_red else R.drawable.ic_love_gray
                    )

                    imvAlbum.setOnClickListener {
                        callback.onAlbumImageClick(musicItem.albumId ?: 0)
                    }
                    buttonFavoriteState.imbLove.setOnClickListener {
                        callback.onFavoriteActionClick(
                            musicItem.id ?: 0,
                            musicItem.isFavorite ?: false,
                            buttonFavoriteState
                        )
                    }

                    val popupWindow = initPopupWindow(
                        itemView,
                        musicItem.id ?: 0,
                        musicItem.playlistDetailId ?: 0
                    )
                    imbKebab.setOnClickListener {
                        popupWindow.showAsDropDown(it)
                    }
                }
            } else {
                with(binding as MusicItemBinding) {
                    Glide.with(itemView.context).load(musicItem.albumPhoto).into(imvAlbum)
                    tvTitle.text = musicItem.name
                    tvSinger.text = musicItem.artist?.let { concatenateArtist(it) }
                    tvDuration.text = musicItem.duration?.let { displayDuration(it) }

                    buttonFavoriteState.imbLove.setImageResource(
                        if (musicItem.isFavorite == true) R.drawable.ic_love_red else R.drawable.ic_love_gray
                    )

                    imvAlbum.setOnClickListener {
                        callback.onAlbumImageClick(musicItem.albumId ?: 0)
                    }
                    buttonFavoriteState.imbLove.setOnClickListener {
                        callback.onFavoriteActionClick(
                            musicItem.id ?: 0,
                            musicItem.isFavorite ?: false,
                            buttonFavoriteState
                        )
                    }

                    imbPlaylist.setOnClickListener {
                        SearchPlaylistDialogFragment.getInstance(musicItem.id ?: 0)
                            .show(childFragmentManager, SearchPlaylistDialogFragment.TAG)
                    }
                }
            }
            itemView.setOnClickListener {
                callback.onItemClick(musicItem.id ?: 0)
            }
        }

        private fun initPopupWindow(
            itemView: View,
            musicId: Int,
            playlistDetailId: Int
        ): PopupWindow {
            val inflater: LayoutInflater =
                itemView.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.popup_kebab_playlist, null)

            val popupWindow = PopupWindow(
                view,
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                true
            )

            val itemFirst = view.findViewById<RelativeLayout>(R.id.item_first)
            val tvFirst = view.findViewById<TextView>(R.id.tv_first)
            tvFirst.text = itemView.context.getString(R.string.add_to_playlist)
            itemFirst.setOnClickListener {
                SearchPlaylistDialogFragment.getInstance(musicId)
                    .show(childFragmentManager, SearchPlaylistDialogFragment.TAG)
                popupWindow.dismiss()
            }

            val itemSecond = view.findViewById<RelativeLayout>(R.id.item_second)
            val tvSecond = view.findViewById<TextView>(R.id.tv_second)
            tvSecond.text = itemView.context.getString(R.string.delete)
            itemSecond.setOnClickListener {
                DeleteMusicFromPlaylistDialogFragment.getInstance(playlistDetailId)
                    .show(childFragmentManager, DeleteMusicFromPlaylistDialogFragment.TAG)
                popupWindow.dismiss()
            }

            return popupWindow
        }
    }

    interface MusicAdapterCallback {
        fun onItemClick(musicId: Int)
        fun onAlbumImageClick(albumId: Int)
        fun onFavoriteActionClick(
            musicId: Int,
            isFavorite: Boolean,
            buttonFavoriteState: FavoriteStateButtonBinding
        )

        fun addItemToPlaylist(musicId: Int)
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Music> = object : DiffUtil.ItemCallback<Music>() {
            override fun areItemsTheSame(
                oldItem: Music, newItem: Music
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: Music, newItem: Music
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
