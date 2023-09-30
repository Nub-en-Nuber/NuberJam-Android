package com.example.nuberjam.ui.main.library

import android.app.ActionBar.LayoutParams
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.nuberjam.R
import com.example.nuberjam.data.Result
import com.example.nuberjam.databinding.SingleFormDialogBinding
import com.example.nuberjam.utils.FormValidation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddPlaylistDialogFragment : DialogFragment() {

    private var _binding: SingleFormDialogBinding? = null
    private val binding get() = _binding!!

    private var onAddPlaylistDialogListener: OnAddPlaylistDialogListener? = null

    private val viewModel: LibraryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = SingleFormDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initLayout()
        playlistNameCounter()
    }

    private fun initLayout() {
        binding.apply {
            tvTitle.text = getString(R.string.add_new_playlist)
            tilDialog.hint = getString(R.string.playlist_name)
            btnDialog.text = getString(R.string.save)
            binding.tvCounter.text = getString(R.string.playlist_name_counter, "0")

            tvConfirmation.visibility = View.GONE
            tvCounter.visibility = View.VISIBLE
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE);

        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun playlistNameCounter() {
        binding.etDialog.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                var isError = false

                val playlistName = s.toString().trim()

                binding.tvCounter.text =
                    getString(R.string.playlist_name_counter, playlistName.length.toString())

                if (playlistName.isEmpty()) {
                    isError = true
                    binding.etDialog.error = getString(R.string.form_empty_message)
                } else if (!FormValidation.isPlaylistNameValid(playlistName)) {
                    isError = true
                    binding.etDialog.error = getString(R.string.playlist_name_max_word)
                    binding.tvCounter.setTextColor(resources.getColor(R.color.error_main))
                } else {
                    isError = false
                    binding.tvCounter.setTextColor(resources.getColor(R.color.primary_gray))
                }


                binding.btnDialog.setOnClickListener {
                    if (!isError) {
                        getAccountId(playlistName)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}

        })
    }

    private fun getAccountId(playlistName: String) {
        viewModel.getAccountState().observe(viewLifecycleOwner) { account ->
            if (account != null) {
                addPlaylistToRemote(playlistName, account.id)
            }
        }
    }

    private fun addPlaylistToRemote(playlistName: String, accountId: Int) {
        viewModel.addPlaylist(playlistName, accountId).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Success -> {
                        val isSuccessAddPlaylist = result.data
                        if (isSuccessAddPlaylist) onAddPlaylistDialogListener?.refreshParent()
                        dialog?.dismiss()
                    }

                    else -> {}
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val fragment = parentFragment
        if (fragment is LibraryFragment) {
            this.onAddPlaylistDialogListener = fragment.onAddPlaylistDialogListener
        }
    }

    override fun onDetach() {
        super.onDetach()
        this.onAddPlaylistDialogListener = null
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    interface OnAddPlaylistDialogListener {
        fun refreshParent()
    }
}