package com.example.nuberjam.ui.main.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.nuberjam.R
import com.example.nuberjam.databinding.FragmentLibraryBinding
import com.example.nuberjam.ui.customview.CustomSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LibraryFragment : Fragment() {

    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LibraryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbar()
        setData()
        setFavoriteItem()
    }

    private fun setToolbar() {
        val searchButton = binding.appbar.btnSearch
        val viewTypeButton = binding.appbar.btnViewType
        val plusButton = binding.appbar.btnPlus

        searchButton.setOnClickListener {
            // TODO: navigate to search
            Toast.makeText(requireActivity(), "You clicked me.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setData() {
        viewModel.getAccountState().observe(viewLifecycleOwner) { account ->
            if (account != null) {
                // TODO: MP-370
            }
        }
    }

    private fun setFavoriteItem() {
        binding.apply {
            favoriteItem.tvPlaylistName.text = getString(R.string.liked_song)
            favoriteItem.tvPlaylistType.text = getString(R.string.favorite)
            Glide.with(requireActivity()).load(R.drawable.favorite_pic)
                .into(favoriteItem.ivPlaylistImage)

            favoriteItem.cvLibraryItem.setOnClickListener {
                // TODO: Navigate to Favorite Screen
            }
        }

    }

    private fun showNoData() {
        binding.favoriteItem.cvLibraryItem.visibility = View.VISIBLE
        binding.rvMusicList.visibility = View.GONE
        binding.shimmerLoading.shimmerLibrary.visibility = View.GONE
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

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.favoriteItem.cvLibraryItem.visibility = View.GONE
            binding.rvMusicList.visibility = View.GONE
            binding.shimmerLoading.shimmerLibrary.visibility = View.VISIBLE
        } else {
            binding.favoriteItem.cvLibraryItem.visibility = View.VISIBLE
            binding.rvMusicList.visibility = View.VISIBLE
            binding.shimmerLoading.shimmerLibrary.visibility = View.GONE
        }
    }
}