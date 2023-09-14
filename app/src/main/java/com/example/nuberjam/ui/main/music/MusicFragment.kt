package com.example.nuberjam.ui.main.music

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.nuberjam.R
import com.example.nuberjam.data.Result
import com.example.nuberjam.databinding.FragmentMusicBinding
import com.example.nuberjam.ui.customview.CustomSnackbar
import com.example.nuberjam.utils.Helper
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MusicFragment : Fragment() {

    private var _binding: FragmentMusicBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MusicViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMusicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.incShimmerLoading.shimmerMusic.startShimmerAnimation()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showNavBar(false)
        setArgs()
        showSnackbarObserve()
        setData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        showNavBar(true)
    }

    private fun showNavBar(isVisible: Boolean) {
        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        navBar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    private fun setArgs() {
        val args: MusicFragmentArgs by navArgs()
        viewModel.musicId = args.musicId
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

    private fun setData() {
        viewModel.getAccountState().observe(viewLifecycleOwner) { account ->
            if (account != null) {
                readDetailMusicObserve(account.id)
            }
        }
    }

    private fun readDetailMusicObserve(accountId: Int) {
        viewModel.readDetailMusic(accountId).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }

                    is Result.Success -> {
                        showLoading(false)
                        val musicData = result.data
//                        setView(musicData)
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

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.content.visibility = View.GONE
            binding.incShimmerLoading.shimmerMusic.visibility = View.VISIBLE
        } else {
            binding.content.visibility = View.VISIBLE
            binding.incShimmerLoading.shimmerMusic.visibility = View.GONE
        }
    }
}