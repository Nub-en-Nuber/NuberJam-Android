package com.example.nuberjam.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.nuberjam.databinding.FragmentHomeBinding
import com.example.nuberjam.ui.customview.CustomSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showSnackbarObserve()

//        TODO: Dont forget to call Snackbar when error using code below
//        viewModel.setSnackbar(result.error, CustomSnackbar.STATE_ERROR)
    }

    override fun onResume() {
        super.onResume()
        binding.shimmerLoading.shimmerAlbum.startShimmerAnimation()
        binding.shimmerLoading.shimmerMusic.startShimmerAnimation()
    }


    private fun showNoData() {
        binding.tvDataNotAvailable.visibility = View.VISIBLE
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
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.rvMusicAlbum.visibility = View.GONE
            binding.rvMusicAlbum.visibility = View.GONE
            binding.shimmerLoading.shimmerLayout.visibility = View.VISIBLE
        } else {
            binding.rvMusicAlbum.visibility = View.VISIBLE
            binding.rvMusicAlbum.visibility = View.VISIBLE
            binding.shimmerLoading.shimmerLayout.visibility = View.GONE
        }
        binding.tvDataNotAvailable.visibility = View.GONE
    }

}