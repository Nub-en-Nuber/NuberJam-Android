package com.example.nuberjam.ui.main.library.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.nuberjam.R
import com.example.nuberjam.data.Result
import com.example.nuberjam.data.model.Music
import com.example.nuberjam.databinding.FavoriteStateButtonBinding
import com.example.nuberjam.databinding.FragmentDetailLibraryBinding
import com.example.nuberjam.ui.customview.CustomSnackbar
import com.example.nuberjam.ui.main.adapter.MusicAdapter
import com.example.nuberjam.utils.Helper
import com.example.nuberjam.utils.LibraryDetailType
import com.example.nuberjam.utils.extensions.collectLifecycleFlow
import com.example.nuberjam.utils.extensions.invisible
import com.example.nuberjam.utils.extensions.showNuberJamDefaultState
import com.example.nuberjam.utils.extensions.showNuberJamEmptyState
import com.example.nuberjam.utils.extensions.showNuberJamErrorState
import com.example.nuberjam.utils.extensions.showNuberJamLoadingState
import com.example.nuberjam.utils.extensions.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailLibraryFragment : Fragment() {

    private var _binding: FragmentDetailLibraryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailLibraryViewModel by viewModels()

    private lateinit var musicAdapter: MusicAdapter

    private var favoriteButtonBinding: FavoriteStateButtonBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAppbar()
        setupRecyclerView()
        initDataObserver()
    }

    private fun setupAppbar() {
        val toolbar: Toolbar = binding.appbar.toolbar
        toolbar.navigationIcon = ContextCompat.getDrawable(
            requireContext(), R.drawable.ic_back_gray
        )
        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.appbar.btnSearch.setOnClickListener {
            // TODO: navigate to search
            Toast.makeText(requireActivity(), "You clicked me.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        musicAdapter = MusicAdapter(object : MusicAdapter.MusicAdapterCallback {
            override fun onItemClick(musicId: Int) {
                navigateToDetailMusic(musicId)
            }

            override fun onAlbumImageClick(albumId: Int) {
            }

            override fun onFavoriteActionClick(
                musicId: Int,
                isFavorite: Boolean,
                buttonFavoriteState: FavoriteStateButtonBinding
            ) {
                favoriteButtonBinding = buttonFavoriteState
                if (isFavorite) {
                    viewModel.updateFavoriteData(musicId, false)
                } else {
                    viewModel.updateFavoriteData(musicId, true)
                }
            }

            override fun onPlaylistActionClick(musicId: Int) {
            }

            override fun addItemToPlaylist(musicId: Int) {
            }

            override fun deleteItemFromPlaylist(musicId: Int) {
            }
        })
        binding.rvMusicList.apply {
            adapter = musicAdapter
            layoutManager = LinearLayoutManager(requireActivity())
        }
    }

    private fun navigateToDetailMusic(musicId: Int) {
        val toMusicFragment =
            DetailLibraryFragmentDirections.actionDetailLibraryFragmentToMusicFragment()
        toMusicFragment.musicId = musicId
        findNavController().navigate(toMusicFragment)
    }

    private fun initDataObserver() {
        when (viewModel.libraryViewType) {
            LibraryDetailType.Favorite -> {
                binding.appbar.tvLibraryAppbar.text = getString(R.string.liked_song)
                viewLifecycleOwner.collectLifecycleFlow(viewModel.favoriteState) { result ->
                    when (result) {
                        is Result.Loading -> binding.msvPlaylistOuter.showNuberJamLoadingState()
                        is Result.Success -> {
                            val data = result.data
                            setViewState(getString(R.string.liked_song), data.size, null, data)
                        }

                        is Result.Error -> showErrorState(
                            result.errorCode,
                            viewModel::getFavoriteData
                        )

                        else -> {}
                    }
                }
            }

            LibraryDetailType.Album -> {
                binding.appbar.tvLibraryAppbar.text = getString(R.string.album)
                viewLifecycleOwner.collectLifecycleFlow(viewModel.albumState) { result ->
                    when (result) {
                        is Result.Loading -> binding.msvPlaylistOuter.showNuberJamLoadingState()
                        is Result.Success -> {
                            val data = result.data
                            setViewState(data.name, data.music?.size, data.photo, data.music)
                        }

                        is Result.Error -> showErrorState(
                            result.errorCode,
                            viewModel::getAlbumData
                        )

                        else -> {}
                    }
                }
            }

            LibraryDetailType.Playlist -> {
                with(binding) {
                    val popupMenu = initPopupMenu(imvCover.imbKebab)
                    appbar.tvLibraryAppbar.text = getString(R.string.playlist)
                    imvCover.imbKebab.visible()
                    imvCover.imbKebab.setOnClickListener {
                        popupMenu.show()
                    }
                    imvCover.ivGridImage.setOnClickListener {
                        // TODO: MOVE TO EDIT PHOTO PAGE
                        Toast.makeText(requireActivity(), "Move to edit image", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                viewLifecycleOwner.collectLifecycleFlow(viewModel.playlistState) { result ->
                    when (result) {
                        is Result.Loading -> binding.msvPlaylistOuter.showNuberJamLoadingState()
                        is Result.Success -> {
                            val data = result.data
                            setViewState(
                                data.info.name,
                                data.music.size,
                                data.info.photo,
                                data.music
                            )
                        }

                        is Result.Error -> showErrorState(
                            result.errorCode,
                            viewModel::getPlaylistDetailData
                        )

                        else -> {}
                    }
                }
            }
        }
        showSnackbarObserve()
        observeAddDeleteFavoriteState()
    }

    private fun initPopupMenu(view: View): PopupMenu {
        val popupMenu = PopupMenu(requireActivity(), view)
        popupMenu.inflate(R.menu.kebab_playlist_menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.edit_playlist -> {
                    // TODO: SHOW EDIT PLAYLIST NAME DIALOG
                    Toast.makeText(requireActivity(), "Edit Playlist", Toast.LENGTH_SHORT)
                        .show()
                    true
                }

                R.id.delete_playlist -> {
                    // TODO: SHOW DELETE PLAYLIST CONFIRMATION DIALOG
                    Toast.makeText(requireActivity(), "Delete Playlist", Toast.LENGTH_SHORT)
                        .show()
                    true
                }

                else -> false
            }
        }
        return popupMenu
    }

    private fun setViewState(
        title: String?,
        dataSize: Int?,
        image: String?,
        listMusic: List<Music>?
    ) {
        with(binding) {
            msvPlaylistOuter.showNuberJamDefaultState()
            imvCover.tvLibraryTitle.text = title
            imvCover.tvLibraryType.text = getString(R.string.total_song, dataSize)
            if (image == null) {
                imvCover.ivGridImage.setImageResource(R.drawable.favorite_pic)
            } else {
                Glide.with(requireActivity()).load(image)
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .error(R.drawable.ic_profile_placeholder)
                    .into(imvCover.ivGridImage)
            }
            if (listMusic?.isEmpty() == true) {
                msvPlaylistInner.showNuberJamEmptyState(
                    lottieJson = null,
                    emptyMessage = getString(R.string.no_liked_song)
                )
            } else {
                musicAdapter.submitList(listMusic)
            }
        }
    }

    private fun showErrorState(errorCode: Int, errorAction: () -> Unit) {
        binding.msvPlaylistOuter.showNuberJamErrorState(
            errorMessage = Helper.getApiErrorMessage(
                requireActivity(),
                errorCode
            ),
            onButtonClicked = errorAction
        )
        viewModel.setSnackbar(
            Helper.getApiErrorMessage(requireActivity(), errorCode),
            CustomSnackbar.STATE_ERROR
        )
    }

    private fun observeAddDeleteFavoriteState() {
        viewLifecycleOwner.collectLifecycleFlow(viewModel.addDeleteFavoriteState) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        updateFavoriteState(isLoading = true)
                    }

                    is Result.Success -> {
                        updateFavoriteState(isLoading = false, isSuccess = true)
                        viewModel.getFavoriteData()
                    }

                    is Result.Error -> {
                        updateFavoriteState(isLoading = false, isSuccess = false)
                        viewModel.setSnackbar(
                            Helper.getApiErrorMessage(requireActivity(), result.errorCode),
                            CustomSnackbar.STATE_ERROR
                        )
                    }
                }
            }
        }
    }

    private fun updateFavoriteState(isLoading: Boolean, isSuccess: Boolean = false) {
        if (!isLoading) {
            favoriteButtonBinding?.loading?.invisible()
            val selectedView = favoriteButtonBinding?.imbLove
            selectedView?.visible()
            if (isSuccess) {
                selectedView?.setImageResource(
                    R.drawable.ic_love_red
                )
            } else {
                selectedView?.setImageResource(
                    R.drawable.ic_love_gray
                )
            }
        } else {
            favoriteButtonBinding?.loading?.visible()
            favoriteButtonBinding?.imbLove?.invisible()
        }
    }

    private fun showSnackbarObserve() {
        viewModel.snackbarState.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { snackbarState ->
                val customSnackbar =
                    CustomSnackbar.build(layoutInflater, binding.root, snackbarState.length)
                customSnackbar.setMessage(snackbarState.message)
                customSnackbar.setState(snackbarState.state)
                customSnackbar.show()
            }
        }
    }
}