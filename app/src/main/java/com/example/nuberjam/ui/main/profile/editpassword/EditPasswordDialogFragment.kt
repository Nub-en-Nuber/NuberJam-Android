package com.example.nuberjam.ui.main.profile.editpassword

import android.app.ActionBar
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.view.isInvisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.nuberjam.R
import com.example.nuberjam.data.Result
import com.example.nuberjam.data.model.Account
import com.example.nuberjam.databinding.PasswordDialogBinding
import com.example.nuberjam.ui.main.profile.UpdateAccountViewModel
import com.example.nuberjam.utils.FormValidation
import com.example.nuberjam.utils.extensions.launchLifecycleScopeOnStarted
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditPasswordDialogFragment : DialogFragment() {

    private var _binding: PasswordDialogBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UpdateAccountViewModel by viewModels()

    private var newPassword = ""
    private var isNewPasswordValid = false
    private var confirmNewPassword = ""
    private var isConfirmNewPasswordValid = false

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
        _binding = PasswordDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLayout()
        setupEvent()
        observeState()
    }

    private fun initLayout() {
        binding.apply {
            tvTitle.text = getString(R.string.change_password)
            btnDialog.text = getString(R.string.edit)
        }
    }

    private fun observeState() {
        viewLifecycleOwner.launchLifecycleScopeOnStarted {
            viewModel.loginState.collect { result ->
                if (result != null) {
                    showLoading(result is Result.Loading)
                    when (result) {
                        is Result.Success -> {
                            if (result.data) {
                                binding.etPassword.error = null
                                viewModel.updateAccount(Account(password = newPassword))
                            } else {
                                binding.etPassword.error =
                                    getString(R.string.current_password_is_incorrect)
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
        viewLifecycleOwner.launchLifecycleScopeOnStarted {
            viewModel.updateAccountState.collect { result ->
                if (result != null) {
                    showLoading(result is Result.Loading)
                    when (result) {
                        is Result.Success -> dismiss()
                        else -> {}
                    }
                }
            }
        }
    }

    private fun setupEvent() {
        with(binding) {
            etNewPassword.addTextChangedListener {
                validateNewPassword(it.toString())
            }
            etConfirmPassword.addTextChangedListener {
                validateConfirmNewPassword(it.toString())
            }
        }
        binding.btnDialog.setOnClickListener {
            if (isNewPasswordValid && isConfirmNewPasswordValid) {
                viewModel.validateCurrentPassword(binding.etPassword.text.toString())
            }
        }
    }

    private fun validateConfirmNewPassword(text: String) {
        confirmNewPassword = text
        if (confirmNewPassword.isEmpty()) {
            isConfirmNewPasswordValid = false
        } else if (!FormValidation.isPasswordSame(newPassword, confirmNewPassword)) {
            isConfirmNewPasswordValid = false
            binding.etConfirmPassword.error = getString(R.string.form_password_not_same)
        } else {
            isConfirmNewPasswordValid = true
            binding.etConfirmPassword.error = null
        }
    }

    private fun validateNewPassword(text: String) {
        newPassword = text
        if (newPassword.isEmpty()) {
            isNewPasswordValid = false
        } else if (!FormValidation.isPasswordSame(newPassword, confirmNewPassword)) {
            isNewPasswordValid = false
            binding.etConfirmPassword.error = getString(R.string.form_password_not_same)
        } else if (FormValidation.isPasswordSame(newPassword, confirmNewPassword)) {
            isConfirmNewPasswordValid = true
            binding.etConfirmPassword.error = null
        } else {
            binding.etPassword.error = null
        }
        with(binding.passwordReq) {
            isNewPasswordValid = FormValidation.checkPasswordRequirements(
                requireActivity(),
                newPassword,
                ivMinPassword,
                ivNumbers,
                ivUppercase,
                ivLowercase,
            )
        }

    }

    private fun showLoading(isLoading: Boolean) {
        binding.loading.isInvisible = !isLoading
        binding.btnDialog.isInvisible = isLoading
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val TAG = "EditPasswordDialogFragment"
    }
}