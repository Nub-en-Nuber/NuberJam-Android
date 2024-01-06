package com.example.nuberjam.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nuberjam.R
import com.example.nuberjam.data.Result
import com.example.nuberjam.databinding.FavoriteStateButtonBinding
import com.example.nuberjam.databinding.FragmentHomeBinding
import com.example.nuberjam.ui.customview.CustomSnackbar
import com.example.nuberjam.ui.main.adapter.AlbumAdapter
import com.example.nuberjam.ui.main.adapter.MusicAdapter
import com.example.nuberjam.utils.BundleKeys
import com.example.nuberjam.utils.Helper
import com.example.nuberjam.utils.LibraryDetailType
import com.example.nuberjam.utils.extensions.collectLifecycleFlow
import com.example.nuberjam.utils.extensions.invisible
import com.example.nuberjam.utils.extensions.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var musicAdapter: MusicAdapter

    private lateinit var albumAdapter: AlbumAdapter

    private var favoriteButtonBinding: FavoriteStateButtonBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbar()
        setRecyclerView()
        showSnackbarObserve()
        setData()
    }

    private fun setData() {
        viewModel.getAccountState().observe(viewLifecycleOwner) { account ->
            if (account != null) {
                readAllMusicObserve(account.id)
            }
        }
        readAllAlbumObserve()
    }

    private fun readAllAlbumObserve() {
        viewModel.readAllAlbum().observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }

                    is Result.Success -> {
                        showLoading(false)
                        val data = result.data
                        if (data.isNotEmpty()) {
                            albumAdapter.submitList(data.shuffled())
                        } else {
                            showNoData()
                        }
                    }

                    is Result.Error -> {
                        showLoading(false)
                        viewModel.setSnackbar(
                            Helper.getApiErrorMessage(requireActivity(), result.errorCode),
                            CustomSnackbar.STATE_ERROR
                        )
                    }
                }
            }
        }
    }

    private fun readAllMusicObserve(accountId: Int) {
        viewModel.readAllMusic(accountId).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }

                    is Result.Success -> {
                        showLoading(false)
                        val data = result.data
                        if (data.isNotEmpty()) {
                            musicAdapter.submitList(data)
                        } else {
                            showNoData()
                        }
                    }

                    is Result.Error -> {
                        showLoading(false)
                        viewModel.setSnackbar(
                            Helper.getApiErrorMessage(requireActivity(), result.errorCode),
                            CustomSnackbar.STATE_ERROR
                        )
                    }
                }
            }
        }
    }

    private fun setRecyclerView() {
        albumAdapter = AlbumAdapter {
            goToDetailLibraryPage(LibraryDetailType.Album, it.id ?: 0)
        }
        binding.rvMusicAlbum.apply {
            layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
            adapter = albumAdapter
        }
        musicAdapter = MusicAdapter(object : MusicAdapter.MusicAdapterCallback {
            override fun onItemClick(musicId: Int) {
                navigateToDetailMusic(musicId)
            }

            override fun onAlbumImageClick(albumId: Int) {
                goToDetailLibraryPage(LibraryDetailType.Album, albumId)
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
            layoutManager = LinearLayoutManager(activity)
            adapter = musicAdapter
        }
    }

    private fun navigateToDetailMusic(musicId: Int) {
        val toMusicFragment = HomeFragmentDirections.actionNavigationHomeToMusicFragment()
        toMusicFragment.musicId = musicId
        findNavController().navigate(toMusicFragment)
    }

    override fun onResume() {
        super.onResume()
        binding.shimmerLoading.shimmerAlbum.startShimmerAnimation()
        binding.shimmerLoading.shimmerMusic.startShimmerAnimation()
    }

    private fun setToolbar() {
        val searchButton = binding.appbar.btnSearch

        searchButton.setOnClickListener {
            // TODO: navigate to search
            Toast.makeText(requireActivity(), "You clicked me.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showNoData() {
        binding.ltDataNotAvailable.visibility = View.VISIBLE
        binding.rvMusicAlbum.visibility = View.GONE
        binding.rvMusicAlbum.visibility = View.GONE
        binding.shimmerLoading.shimmerLayout.visibility = View.GONE
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

        viewLifecycleOwner.collectLifecycleFlow(viewModel.addDeleteFavoriteState) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        updateFavoriteState(isLoading = true)
                    }
                    is Result.Success -> {
                        updateFavoriteState(isLoading = false, isSuccess = true)
                        setData()
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
            if (isSuccess){
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

    private fun goToDetailLibraryPage(viewType: LibraryDetailType, albumId: Int = 0) {
        val args = Bundle().apply {
            putSerializable(BundleKeys.LIBRARY_VIEW_TYPE_KEY, viewType)
            putInt(BundleKeys.ALBUM_ID_KEY, albumId)
        }
        findNavController().navigate(R.id.action_navigation_home_to_detailLibraryFragment, args)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.rvMusicAlbum.visibility = View.GONE
            binding.rvMusicList.visibility = View.GONE
            binding.shimmerLoading.shimmerLayout.visibility = View.VISIBLE
        } else {
            binding.rvMusicAlbum.visibility = View.VISIBLE
            binding.rvMusicList.visibility = View.VISIBLE
            binding.shimmerLoading.shimmerLayout.visibility = View.GONE
        }
        binding.ltDataNotAvailable.visibility = View.GONE
    }
}