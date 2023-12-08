package com.example.nuberjam.ui.main.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.nuberjam.R
import com.example.nuberjam.data.model.Account
import com.example.nuberjam.databinding.FragmentProfileBinding
import com.example.nuberjam.ui.customview.CustomSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()

    private var account = Account()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getAccountState().observe(viewLifecycleOwner) {
            account = it
            setupView()
        }

        setupAction()
        showSnackBarObserve()
    }

    private fun setupAction() {
        with(binding) {
            btnLogout.setOnClickListener {
                logoutProcess()
            }

            imbUsername.setOnClickListener {
                // TODO: MP-415
            }

            imbName.setOnClickListener {
                // TODO: MP-416
            }

            imbEmail.setOnClickListener {
                // TODO: MP-417
            }

            imbChangePassword.setOnClickListener {
                // TODO: MP-418
            }

            imbTnc.setOnClickListener {
                findNavController().navigate(R.id.action_navigation_profile_to_tncFragment)
            }

            tvDeleteAccount.setOnClickListener {
                // TODO: MP-419
            }

            imvProfile.setOnClickListener {
                findNavController().navigate(R.id.action_navigation_profile_to_editPhotoFragment)
            }

//            viewModel.setSnackbar(
//                "", CustomSnackbar.STATE_SUCCESS
//            )
        }
    }

    private fun setupView() {
        checkAccountArtist()
        setUserProfile()
    }

    private fun checkAccountArtist() {
        binding.imvVerified.isVisible = account.isArtist
        binding.tvDeleteAccount.isVisible = !account.isArtist
    }

    private fun setUserProfile() {
        with(binding) {
            Glide.with(requireActivity()).load(account.photo).error(R.drawable.ic_profile_placeholder).into(imvProfile)
            tvName.text = account.name
            tvYourUsername.text = account.username
            tvYourName.text = account.name
            tvYourEmail.text = account.email
        }
    }

    private fun logoutProcess() {
        viewModel.saveLoginState(false)
        viewModel.clearAccountState()

        val toAuthActivity = ProfileFragmentDirections.actionNavigationProfileToAuthActivity()
        toAuthActivity.username = account.username
        findNavController().navigate(toAuthActivity)
        requireActivity().finish()
    }

    private fun showSnackBarObserve() {
        viewModel.snackbarState.observe(requireActivity()) { event ->
            event.getContentIfNotHandled()?.let { snackBarState ->
                val customSnackBar =
                    CustomSnackbar.build(layoutInflater, binding.root, snackBarState.length)
                customSnackBar.setMessage(snackBarState.message)
                customSnackBar.setState(snackBarState.state)
                customSnackBar.show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}