package com.example.nuberjam.ui.main.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.nuberjam.R
import com.example.nuberjam.data.Result
import com.example.nuberjam.databinding.FragmentLibraryBinding
import com.example.nuberjam.ui.customview.CustomSnackbar
import com.example.nuberjam.ui.main.adapter.GridPlaylistAdapter
import com.example.nuberjam.ui.main.adapter.LinearPlaylistAdapter
import com.example.nuberjam.utils.BundleKeys
import com.example.nuberjam.utils.Constant
import com.example.nuberjam.utils.Helper
import com.example.nuberjam.utils.LibraryDetailType
import com.example.nuberjam.utils.extensions.showNuberJamDefaultState
import com.example.nuberjam.utils.extensions.showNuberJamLoadingState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LibraryFragment : Fragment() {

    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LibraryViewModel by viewModels()

    private val linearAdapter: LinearPlaylistAdapter by lazy {
        LinearPlaylistAdapter {
            goToDetailLibraryPage(LibraryDetailType.Playlist, it.id)
        }
    }

    private val gridAdapter: GridPlaylistAdapter by lazy {
        GridPlaylistAdapter {
            goToDetailLibraryPage(LibraryDetailType.Playlist, it.id)
        }
    }

    lateinit var onAddPlaylistDialogListener: AddPlaylistDialogFragment.OnAddPlaylistDialogListener

    private var libraryViewType = Constant.LIBRARY_LINEAR_TYPE

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbar()
        showSnackbarObserve()
        setFavoriteItem()
        initAction()
        setData()
        refreshFragment()
    }

    private fun initAction() {
        with(binding) {
            changeLibraryTypeLayout()
            favoriteItem.favoriteLinearItem.cvLibraryItem.setOnClickListener {
                goToDetailLibraryPage(LibraryDetailType.Favorite)
            }

            favoriteItem.favoriteGridItem.cvPlaylistItem.setOnClickListener {
                goToDetailLibraryPage(LibraryDetailType.Favorite)
            }
        }
    }

    private fun setToolbar() {
        val viewTypeButton = binding.appbar.btnViewType
        val plusButton = binding.appbar.btnPlus

        viewTypeButton.setOnClickListener {
            libraryViewType =
                if (libraryViewType == Constant.LIBRARY_LINEAR_TYPE) Constant.LIBRARY_GRID_TYPE
                else Constant.LIBRARY_LINEAR_TYPE

            changeLibraryTypeLayout()
        }

        plusButton.setOnClickListener {
            val addPlaylistDialogFragment = AddPlaylistDialogFragment()
            val fragmentManager = childFragmentManager
            addPlaylistDialogFragment.show(
                fragmentManager, AddPlaylistDialogFragment::class.java.simpleName
            )
        }
    }

    private fun setData() {
        readAllPlaylistObserve()
    }

    private fun refreshFragment() {
        onAddPlaylistDialogListener =
            object : AddPlaylistDialogFragment.OnAddPlaylistDialogListener {
                override fun refreshParent() {
                    setData()
                }
            }
    }

    private fun changeLibraryTypeLayout() {
        binding.apply {
            if (libraryViewType == Constant.LIBRARY_LINEAR_TYPE) {
                favoriteItem.favoriteLinearItem.cvLibraryItem.visibility = View.VISIBLE
                favoriteItem.favoriteGridItem.clGridItem.visibility = View.GONE
                appbar.btnViewType.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(), R.drawable.ic_gridview_gray_24
                    )
                )
                setLinearRecyclerView()
            } else {
                favoriteItem.favoriteLinearItem.cvLibraryItem.visibility = View.GONE
                favoriteItem.favoriteGridItem.clGridItem.visibility = View.VISIBLE
                appbar.btnViewType.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(), R.drawable.ic_listview_gray_24
                    )
                )
                setGridRecyclerView()
            }
        }
    }

    private fun readAllPlaylistObserve() {
        viewModel.readAllPlaylist().observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.msvLibrary.showNuberJamLoadingState()
                    }

                    is Result.Success -> {
                        binding.msvLibrary.showNuberJamDefaultState()
                        val data = result.data.reversed()
                        if (data.isNotEmpty()) {
                            linearAdapter.submitList(data)
                            gridAdapter.submitList(data)
                        }
                    }

                    is Result.Error -> {
                        binding.msvLibrary.showNuberJamDefaultState()
                        viewModel.setSnackbar(
                            Helper.getApiErrorMessage(requireActivity(), result.errorCode),
                            CustomSnackbar.STATE_ERROR
                        )

                        // If you want to use Error State
//                        binding.msvLibrary.showNuberJamErrorState(
//                            emptyMessage = "Gaada data cuy!",
//                            onButtonClicked = {
//                                setData()
//                            }
//                        )
                    }
                }
            }
        }
    }

    private fun setFavoriteItem() {
        binding.apply {
            favoriteItem.favoriteLinearItem.tvPlaylistName.text = getString(R.string.liked_song)
            favoriteItem.favoriteLinearItem.tvPlaylistType.text = getString(R.string.favorite)
            Glide.with(requireActivity()).load(R.drawable.favorite_pic)
                .into(favoriteItem.favoriteLinearItem.ivPlaylistImage)

            favoriteItem.favoriteGridItem.tvLibraryTitle.text = getString(R.string.liked_song)
            favoriteItem.favoriteGridItem.tvLibraryType.text = getString(R.string.favorite)
            Glide.with(requireActivity()).load(R.drawable.favorite_pic)
                .into(favoriteItem.favoriteGridItem.ivGridImage)
        }
    }

    private fun setLinearRecyclerView() {
        binding.rvPlaylist.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = linearAdapter
        }
    }

    private fun setGridRecyclerView() {
        binding.rvPlaylist.apply {
            layoutManager = GridLayoutManager(requireActivity(), 2)
            adapter = gridAdapter
        }
    }

    private fun goToDetailLibraryPage(viewType: LibraryDetailType, playlistId: Int = 0) {
        val args = Bundle().apply {
            putSerializable(BundleKeys.LIBRARY_VIEW_TYPE_KEY, viewType)
            putInt(BundleKeys.PLAYLIST_ID_KEY, playlistId)
        }
        findNavController().navigate(R.id.action_navigation_library_to_detailLibraryFragment, args)
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