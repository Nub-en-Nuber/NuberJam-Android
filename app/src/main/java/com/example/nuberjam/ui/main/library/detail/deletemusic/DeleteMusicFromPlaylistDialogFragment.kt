package com.example.nuberjam.ui.main.library.detail.deletemusic

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
import com.example.nuberjam.R
import com.example.nuberjam.data.Result
import com.example.nuberjam.databinding.ConfirmationDialogBinding
import com.example.nuberjam.ui.main.library.detail.DetailLibraryFragment
import com.example.nuberjam.ui.main.library.detail.UpdatePlaylistViewModel
import com.example.nuberjam.utils.BundleKeys
import com.example.nuberjam.utils.Helper
import com.example.nuberjam.utils.extensions.collectLifecycleFlow
import com.example.nuberjam.utils.extensions.visible
import com.example.nuberjam.utils.extensions.visibleIf
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteMusicFromPlaylistDialogFragment : DialogFragment() {

    private var _binding: ConfirmationDialogBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UpdatePlaylistViewModel by viewModels()

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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ConfirmationDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLayout()
        observeDeleteState()
        binding.btnDialogAccept.setOnClickListener {
            viewModel.deleteMusicFromPlaylist()
        }
        binding.btnDialogCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun observeDeleteState() {
        viewLifecycleOwner.collectLifecycleFlow(viewModel.deleteMusicFromPlaylistState) { result ->
            showLoading(result is Result.Loading)
            when (result) {
                is Result.Success -> {
                    setFragmentResult(
                        DetailLibraryFragment.EDIT_PLAYLIST_REQUEST_KEY,
                        bundleOf(BundleKeys.EDIT_PLAYLIST_STATE_KEY to true)
                    )
                    dismiss()
                }

                is Result.Error -> showError(result.errorCode)
                else -> Unit
            }
        }
    }

    private fun initLayout() {
        binding.apply {
            tvTitle.text = getString(R.string.title_delete_song)
            tvConfirmation.text = getString(R.string.music_delete_confirmation)
            btnDialogCancel.text = getString(R.string.cancel)
            btnDialogAccept.text = getString(R.string.yes)
        }
    }

    private fun showError(errorCode: Int) {
        val message = Helper.getApiErrorMessage(requireActivity(), errorCode)
        binding.tvError.text = message
        binding.tvError.visible()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loading.visibleIf { isLoading }
        binding.btnDialogAccept.visibleIf { !isLoading }
        binding.btnDialogCancel.visibleIf { !isLoading }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val TAG = "DeleteMusicFromPlaylistDialogFragment"

        fun getInstance(id: Int): DeleteMusicFromPlaylistDialogFragment =
            DeleteMusicFromPlaylistDialogFragment().apply {
                arguments = Bundle().apply {
                    putInt(BundleKeys.PLAYLIST_DETAIL_ID_KEY, id)
                }
            }
    }
}