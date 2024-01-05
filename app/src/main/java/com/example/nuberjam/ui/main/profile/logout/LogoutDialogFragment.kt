package com.example.nuberjam.ui.main.profile.logout

import android.app.ActionBar
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.nuberjam.R
import com.example.nuberjam.databinding.ConfirmationDialogBinding
import com.example.nuberjam.ui.main.profile.ProfileFragmentDirections
import com.example.nuberjam.ui.main.profile.ProfileViewModel
import com.example.nuberjam.utils.BundleKeys.USERNAME_KEY
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LogoutDialogFragment : DialogFragment() {

    private var _binding: ConfirmationDialogBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    private lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        username = arguments?.getString(USERNAME_KEY) ?: ""
    }

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
        _binding = ConfirmationDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLayout()
        setupEvent()
    }

    private fun initLayout() {
        binding.apply {
            tvTitle.text = getString(R.string.logout_text)
            tvConfirmation.text = getString(R.string.logout_confirmation_text)
            btnDialogCancel.text = getString(R.string.no)
            btnDialogAccept.text = getString(R.string.yes)
        }
    }

    private fun setupEvent() {
        with(binding) {
            btnDialogCancel.setOnClickListener { dismiss() }
            btnDialogAccept.setOnClickListener {
                viewModel.saveLoginState(false)
                viewModel.clearAccountState()

                val toAuthActivity =
                    ProfileFragmentDirections.actionNavigationProfileToAuthActivity()
                toAuthActivity.username = username
                findNavController().navigate(toAuthActivity)
                requireActivity().finish()
                dismiss()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val TAG = "LogoutDialogFragment"

        fun getInstance(username: String): LogoutDialogFragment = LogoutDialogFragment().apply {
            arguments = Bundle().apply {
                putString(USERNAME_KEY, username)
            }
        }
    }
}