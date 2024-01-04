package com.example.nuberjam.ui.main.library.detail.editname

import android.app.ActionBar
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.example.nuberjam.R
import com.example.nuberjam.data.Result
import com.example.nuberjam.databinding.SingleFormDialogBinding
import com.example.nuberjam.ui.main.library.detail.DetailLibraryFragment
import com.example.nuberjam.ui.main.library.detail.UpdatePlaylistViewModel
import com.example.nuberjam.utils.BundleKeys
import com.example.nuberjam.utils.BundleKeys.EDIT_PLAYLIST_STATE_KEY
import com.example.nuberjam.utils.BundleKeys.PLAYLIST_ID_KEY
import com.example.nuberjam.utils.BundleKeys.PLAYLIST_NAME_KEY
import com.example.nuberjam.utils.Helper
import com.example.nuberjam.utils.extensions.collectLifecycleFlow
import com.example.nuberjam.utils.extensions.gone
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditNameDialogFragment : DialogFragment() {

    private var _binding: SingleFormDialogBinding? = null
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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = SingleFormDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLayout()
        observeEditNameState()
        binding.etDialog.addTextChangedListener {
            viewModel.name = it.toString()
        }
        binding.btnDialog.setOnClickListener {
            viewModel.updatePlaylist()
        }
    }

    private fun observeEditNameState() {
        viewLifecycleOwner.collectLifecycleFlow(viewModel.updatePlaylistState) { result ->
            showLoading(result is Result.Loading)
            when (result) {
                is Result.Success -> {
                    setFragmentResult(
                        DetailLibraryFragment.EDIT_PLAYLIST_REQUEST_KEY,
                        bundleOf(EDIT_PLAYLIST_STATE_KEY to true)
                    )
                    dismiss()
                }

                is Result.Error -> showError(result.errorCode)
                else -> Unit
            }
        }
    }

    private fun showError(errorCode: Int) {
        val message = Helper.getApiErrorMessage(requireActivity(), errorCode)
        binding.tilDialog.error = message
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loading.isInvisible = !isLoading
        binding.btnDialog.isInvisible = isLoading
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initLayout() {
        binding.apply {
            tvTitle.text = getString(R.string.title_edit_playlist_name)
            tilDialog.hint = getString(R.string.playlist_name_hint)
            btnDialog.text = getString(R.string.edit)

            etDialog.setText(viewModel.name)

            tilDialog.isCounterEnabled = true
            etDialog.filters = arrayOf(InputFilter.LengthFilter(28))

            val param = btnDialog.layoutParams as ViewGroup.MarginLayoutParams
            param.setMargins(0, 4, 0, 0)
            btnDialog.layoutParams = param

            tvConfirmation.gone()
        }
    }

    companion object {
        const val TAG = "EditNameDialogFragment"

        fun getInstance(id: Int, name: String): EditNameDialogFragment =
            EditNameDialogFragment().apply {
                arguments = Bundle().apply {
                    putInt(PLAYLIST_ID_KEY, id)
                    putString(PLAYLIST_NAME_KEY, name)
                }
            }
    }
}