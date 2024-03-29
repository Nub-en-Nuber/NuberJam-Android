package com.example.nuberjam.ui.main.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.nuberjam.R
import com.example.nuberjam.data.model.Account
import com.example.nuberjam.databinding.FragmentProfileBinding
import com.example.nuberjam.ui.customview.CustomSnackbar
import com.example.nuberjam.ui.main.profile.deleteaccount.DeleteAccountDialogFragment
import com.example.nuberjam.ui.main.profile.editname.EditNameDialogFragment
import com.example.nuberjam.ui.main.profile.editpassword.EditPasswordDialogFragment
import com.example.nuberjam.utils.EditPhotoType
import com.example.nuberjam.ui.main.profile.logout.LogoutDialogFragment
import com.example.nuberjam.utils.BundleKeys
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()

    private var account = Account()

    companion object {
        const val EDIT_REQUEST_KEY = "edit_request_key"
    }

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
        setupFragmentResultListener()
    }

    private fun setupFragmentResultListener() {
        childFragmentManager.setFragmentResultListener(
            EDIT_REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            showEditResultSnackbar(bundle)
        }
        setFragmentResultListener(EDIT_REQUEST_KEY) { _, bundle ->
            showEditResultSnackbar(bundle)
        }
    }

    private fun showEditResultSnackbar(bundle: Bundle) {
        val state = bundle.getBoolean(BundleKeys.EDIT_PROFILE_STATE_KEY)
        if (state) {
            viewModel.setSnackbar(
                getString(R.string.edit_success_message),
                CustomSnackbar.STATE_SUCCESS
            )
        }
    }

    private fun setupAction() {
        with(binding) {
            btnLogout.setOnClickListener {
                openLogoutDialog()
            }

            imbName.setOnClickListener {
                openEditNameDialog()
            }

            imbChangePassword.setOnClickListener {
                val editPasswordDialogFragment = EditPasswordDialogFragment()
                editPasswordDialogFragment.show(
                    childFragmentManager,
                    EditPasswordDialogFragment.TAG
                )
            }

            imbTnc.setOnClickListener {
                findNavController().navigate(R.id.action_navigation_profile_to_tncFragment)
            }

            tvDeleteAccount.setOnClickListener {
                val deleteAccountDialogFragment = DeleteAccountDialogFragment()
                deleteAccountDialogFragment.show(
                    childFragmentManager,
                    DeleteAccountDialogFragment.TAG
                )
            }

            imvProfile.setOnClickListener {
                val toPhotoFragment =
                    ProfileFragmentDirections.actionNavigationProfileToEditPhotoFragment()
                toPhotoFragment.currentPhoto = account.photo
                toPhotoFragment.entryPoint = EditPhotoType.Profile
                findNavController().navigate(toPhotoFragment)
            }
        }
    }

    private fun openLogoutDialog() {
        val logoutDialog = LogoutDialogFragment.getInstance(account.username)
        logoutDialog.show(childFragmentManager, LogoutDialogFragment.TAG)
    }

    private fun openEditNameDialog() {
        val name = binding.tvName.text.toString()
        val editNameDialogFragment = EditNameDialogFragment.getInstance(name)
        editNameDialogFragment.show(childFragmentManager, EditNameDialogFragment.TAG)
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
            Glide.with(requireActivity()).load(account.photo)
                .placeholder(R.drawable.ic_profile_placeholder_96)
                .error(R.drawable.ic_profile_placeholder_96)
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .into(imvProfile)
            tvName.text = account.name
            tvYourUsername.text = account.username
            tvYourName.text = account.name
            tvYourEmail.text = account.email
        }
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