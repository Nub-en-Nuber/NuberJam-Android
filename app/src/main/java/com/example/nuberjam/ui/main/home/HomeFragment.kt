package com.example.nuberjam.ui.main.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nuberjam.databinding.FragmentHomeBinding
import com.example.nuberjam.ui.customview.CustomSnackbar
import com.example.nuberjam.ui.main.adapter.MusicAdapter
import dagger.hilt.android.AndroidEntryPoint
import com.example.nuberjam.data.Result

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var musicAdapter: MusicAdapter

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
                            Log.d("TAG", "readAllMusicObserve: $data")
                            musicAdapter.submitList(data.shuffled())
                        } else {
                            showNoData()
                        }
                    }

                    is Result.Error -> {
                        showLoading(false)
                        viewModel.setSnackbar(result.error, CustomSnackbar.STATE_ERROR)
                    }
                }
            }
        }
    }

    private fun setRecyclerView() {
        musicAdapter = MusicAdapter(object : MusicAdapter.MusicAdapterCallback {
            override fun onItemClick(musicId: Int) {
                navigateToDetailMusic(musicId)
            }
        })
        binding.rvMusicList.apply {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            adapter = musicAdapter
        }
    }

    private fun navigateToDetailMusic(musicId: Int) {
//        TODO: navigate to detail music page with arg
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
            binding.rvMusicList.visibility = View.GONE
            binding.shimmerLoading.shimmerLayout.visibility = View.VISIBLE
        } else {
            binding.rvMusicAlbum.visibility = View.VISIBLE
            binding.rvMusicList.visibility = View.VISIBLE
            binding.shimmerLoading.shimmerLayout.visibility = View.GONE
        }
        binding.tvDataNotAvailable.visibility = View.GONE
    }
}