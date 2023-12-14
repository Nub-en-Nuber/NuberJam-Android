package com.example.nuberjam.ui.main.profile.deleteaccount

import android.app.ActionBar
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.nuberjam.R
import com.example.nuberjam.data.Result
import com.example.nuberjam.data.model.Account
import com.example.nuberjam.databinding.ConfirmationDialogBinding
import com.example.nuberjam.databinding.SingleFormDialogBinding
import com.example.nuberjam.ui.main.profile.ProfileFragmentDirections
import com.example.nuberjam.ui.main.profile.UpdateAccountViewModel
import com.example.nuberjam.ui.main.profile.editname.EditNameDialogFragment
import com.example.nuberjam.utils.Helper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DeleteAccountDialogFragment : DialogFragment() {
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
            ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = SingleFormDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLayout()
        observeDeleteAccountState()
        binding.btnDialog.setOnClickListener {
            viewModel.validateCurrentPassword(binding.etDialog.text.toString())
        }
    }

    private fun observeDeleteAccountState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.deleteAccountState.collect { result ->
                    if (result != null) {
                        showLoading(result is Result.Loading)
                        when (result) {
                            is Result.Success -> {
                                if (result.data) moveToLoginPage()
                            }

                            is Result.Error -> showError(result.errorCode)
                            else -> {}
                        }
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginState.collect { result ->
                    if (result != null) {
                        showLoading(result is Result.Loading)
                        when (result) {
                            is Result.Success -> {
                                if (result.data) viewModel.deleteAccount()
                                else binding.etDialog.error =
                                    getString(R.string.current_password_is_incorrect)
                            }

                            is Result.Error -> showError(result.errorCode)
                            else -> {}
                        }
                    }
                }
            }
        }
    }

    private fun moveToLoginPage() {
        val toAuthActivity = ProfileFragmentDirections.actionNavigationProfileToAuthActivity()
        findNavController().navigate(toAuthActivity)
        requireActivity().finish()
    }

    private fun showError(errorCode: Int) {
        val message = Helper.getApiErrorMessage(requireActivity(), errorCode)
        binding.etDialog.error = message
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
            tvTitle.text = getString(R.string.delete_account)
            tvConfirmation.text = getString(R.string.delete_confirmation)
            tvConfirmation.isInvisible = false
            btnDialog.text = getString(R.string.delete)
            btnDialog.setBackgroundColor(
                ContextCompat.getColor(
                    requireActivity(), R.color.error_main
                )
            )
            tilDialog.hint = getString(R.string.confirm_password_hint)
            etDialog.transformationMethod = PasswordTransformationMethod.getInstance()
        }
    }

    companion object {
        const val TAG = "DeleteAccountDialogFragment"
    }
}