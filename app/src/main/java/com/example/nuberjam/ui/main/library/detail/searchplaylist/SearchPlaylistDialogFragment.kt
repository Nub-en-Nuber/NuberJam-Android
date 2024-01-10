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
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nuberjam.R
import com.example.nuberjam.data.Result
import com.example.nuberjam.databinding.PlaylistDialogBinding
import com.example.nuberjam.ui.main.adapter.LinearPlaylistAdapter
import com.example.nuberjam.ui.main.library.detail.UpdatePlaylistViewModel
import com.example.nuberjam.utils.BundleKeys
import com.example.nuberjam.utils.Helper
import com.example.nuberjam.utils.Helper.debounce
import com.example.nuberjam.utils.extensions.showNuberJamDefaultState
import com.example.nuberjam.utils.extensions.showNuberJamEmptyState
import com.example.nuberjam.utils.extensions.showNuberJamErrorState
import com.example.nuberjam.utils.extensions.showNuberJamLoadingState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchPlaylistDialogFragment : DialogFragment() {

    private var _binding: PlaylistDialogBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UpdatePlaylistViewModel by viewModels()

    private val playlistAdapter: LinearPlaylistAdapter by lazy {
        LinearPlaylistAdapter { playlist ->
            Toast.makeText(requireActivity(), "Playlist Id : ${playlist.id}", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private var query: String = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ActionBar.LayoutParams.MATCH_PARENT,
            ActionBar.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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
                if (result != null)
                    when (result) {
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
                            msvPlaylist.showNuberJamErrorState(
                                errorMessage = Helper.getApiErrorMessage(
                                    requireActivity(),
                                    result.errorCode
                                ),
                                onButtonClicked = {
                                    searchPlaylistObserver()
                                }
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