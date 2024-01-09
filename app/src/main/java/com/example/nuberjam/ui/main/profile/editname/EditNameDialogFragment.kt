package com.example.nuberjam.ui.main.profile.editname

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
import androidx.core.view.isInvisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.nuberjam.R
import com.example.nuberjam.data.Result
import com.example.nuberjam.data.model.Account
import com.example.nuberjam.databinding.SingleFormDialogBinding
import com.example.nuberjam.ui.main.profile.ProfileFragment
import com.example.nuberjam.ui.main.profile.UpdateAccountViewModel
import com.example.nuberjam.utils.BundleKeys.EDIT_PROFILE_STATE_KEY
import com.example.nuberjam.utils.Helper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditNameDialogFragment : DialogFragment() {

    private var _binding: SingleFormDialogBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UpdateAccountViewModel by viewModels()

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
            viewModel.updateAccount(Account(name = viewModel.name))
        }
    }

    private fun observeEditNameState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.updateAccountState.collect { result ->
                    if (result != null) {
                        showLoading(result is Result.Loading)
                        when (result) {
                            is Result.Success -> {
                                setFragmentResult(
                                    ProfileFragment.EDIT_REQUEST_KEY,
                                    bundleOf(EDIT_PROFILE_STATE_KEY to true)
                                )
                                dismiss()
                            }

                            is Result.Error -> showError(result.errorCode)
                            else -> {}
                        }
                    }
                }
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
            tvTitle.text = getString(R.string.title_edit_name)
            tilDialog.hint = getString(R.string.name_hint)
            etDialog.setText(viewModel.name)
            btnDialog.text = getString(R.string.edit)
            tvConfirmation.visibility = View.GONE
        }
    }

    companion object {
        const val TAG = "EditNameDialogFragment"
        const val ARG_NAME_KEY = "arg_name"

        fun getInstance(name: String): EditNameDialogFragment = EditNameDialogFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_NAME_KEY, name)
            }
        }
    }
}