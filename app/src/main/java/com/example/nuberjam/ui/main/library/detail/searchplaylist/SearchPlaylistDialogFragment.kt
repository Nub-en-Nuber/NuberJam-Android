package com.example.nuberjam.ui.main.library.detail.searchplaylist

import android.app.ActionBar
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nuberjam.R
import com.example.nuberjam.data.Result
import com.example.nuberjam.databinding.PlaylistDialogBinding
import com.example.nuberjam.ui.main.adapter.LinearPlaylistAdapter
import com.example.nuberjam.ui.main.library.detail.DetailLibraryFragment
import com.example.nuberjam.ui.main.library.detail.UpdatePlaylistViewModel
import com.example.nuberjam.utils.BundleKeys
import com.example.nuberjam.utils.Helper
import com.example.nuberjam.utils.Helper.debounce
import com.example.nuberjam.utils.extensions.collectLifecycleFlow
import com.example.nuberjam.utils.extensions.gone
import com.example.nuberjam.utils.extensions.showNuberJamDefaultState
import com.example.nuberjam.utils.extensions.showNuberJamEmptyState
import com.example.nuberjam.utils.extensions.showNuberJamErrorState
import com.example.nuberjam.utils.extensions.showNuberJamLoadingState
import com.example.nuberjam.utils.extensions.visible
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SearchPlaylistDialogFragment : DialogFragment() {

    private var _binding: PlaylistDialogBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UpdatePlaylistViewModel by viewModels()

    private val playlistAdapter: LinearPlaylistAdapter by lazy {
        LinearPlaylistAdapter { playlist ->
            viewModel.selectedPlaylistId = playlist.id
            viewModel.checkMusicIsExist()
        }
    }

    private var query: String = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onResume() {
        super.onResume()
        val height = (resources.displayMetrics.heightPixels * 0.70).toInt()

        dialog?.window?.run {
            setLayout(
                ActionBar.LayoutParams.MATCH_PARENT,
                height
            )
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = PlaylistDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        initAction()
        initObserver()
        setupFragmentResultListener()
        searchPlaylistObserver()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initUI() {
        binding.apply {
            tvTitle.text = getString(R.string.title_add_to_playlist)
            tilDialog.hint = getString(R.string.search_playlist)
        }

        binding.rvMusicList.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = playlistAdapter
        }
    }

    private fun initAction() {
        binding.apply {
            etDialog.debounce { text ->
                query = text.toString()
                searchPlaylistObserver()
            }
        }
    }

    private fun searchPlaylistObserver() {
        binding.apply {
            viewModel.searchPlaylist(query).observe(viewLifecycleOwner) { result ->
                if (result != null) when (result) {
                    is Result.Loading -> {
                        msvPlaylist.showNuberJamLoadingState()
                    }

                    is Result.Success -> {
                        msvPlaylist.showNuberJamDefaultState()
                        val data = result.data
                        if (data.isNotEmpty()) {
                            playlistAdapter.submitList(data)
                        } else {
                            msvPlaylist.showNuberJamEmptyState(
                                emptyMessage = getString(R.string.no_playlist)
                            )
                        }
                    }

                    is Result.Error -> {
                        msvPlaylist.showNuberJamErrorState(errorMessage = Helper.getApiErrorMessage(
                            requireActivity(), result.errorCode
                        ), onButtonClicked = {
                            searchPlaylistObserver()
                        })
                    }
                }

            }
        }
    }

    private fun setupFragmentResultListener() {
        childFragmentManager.setFragmentResultListener(
            DetailLibraryFragment.EDIT_PLAYLIST_REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            val state = bundle.getBoolean(BundleKeys.EDIT_PLAYLIST_STATE_KEY)
            binding.msvPlaylist.showNuberJamDefaultState()
            if (state) {
                setFragmentResult(
                    DetailLibraryFragment.EDIT_PLAYLIST_REQUEST_KEY,
                    bundleOf(BundleKeys.EDIT_PLAYLIST_STATE_KEY to true)
                )
                dismiss()
            }
        }
    }

    private fun initObserver() {
        binding.apply {
            viewLifecycleOwner.collectLifecycleFlow(viewModel.checkMusicInPlaylistState) { result ->
                if (result != null) when (result) {
                    is Result.Loading -> {
                        msvPlaylist.showNuberJamLoadingState()
                        tvConfirmation.gone()
                    }

                    is Result.Success -> {
                        if (result.data) {
                            AddMusicConfirmationDialogFragment.getInstance(
                                viewModel.musicId ?: 0,
                                viewModel.selectedPlaylistId
                            )
                                .show(childFragmentManager, AddMusicConfirmationDialogFragment.TAG)
                        } else {
                            viewModel.addMusicToPlaylist()
                        }
                    }

                    is Result.Error -> {
                        msvPlaylist.showNuberJamDefaultState()
                        tvConfirmation.visible()
                        tvConfirmation.text = Helper.getApiErrorMessage(
                            requireActivity(), result.errorCode
                        )
                    }
                }
            }

            viewLifecycleOwner.collectLifecycleFlow(viewModel.addMusicToPlaylistState) { result ->
                if (result != null) when (result) {
                    is Result.Loading -> {
                        msvPlaylist.showNuberJamLoadingState()
                    }

                    is Result.Success -> {
                        msvPlaylist.showNuberJamDefaultState()
                        if (result.data) {
                            setFragmentResult(
                                DetailLibraryFragment.EDIT_PLAYLIST_REQUEST_KEY,
                                bundleOf(BundleKeys.EDIT_PLAYLIST_STATE_KEY to true)
                            )
                            dismiss()
                        }
                    }

                    is Result.Error -> {
                        msvPlaylist.showNuberJamDefaultState()
                        tvConfirmation.visible()
                        tvConfirmation.text = Helper.getApiErrorMessage(
                            requireActivity(), result.errorCode
                        )
                    }
                }
            }
        }
    }

    companion object {
        const val TAG = "SearchPlaylistDialogFragment"

        fun getInstance(musicId: Int): SearchPlaylistDialogFragment =
            SearchPlaylistDialogFragment().apply {
                arguments = Bundle().apply {
                    putInt(BundleKeys.MUSIC_ID_KEY, musicId)
                }
            }
    }
}