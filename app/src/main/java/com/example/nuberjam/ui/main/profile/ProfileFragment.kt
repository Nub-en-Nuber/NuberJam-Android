package com.example.nuberjam.ui.main.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.nuberjam.data.model.Account
import com.example.nuberjam.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()

    private lateinit var account: Account

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
    }

    private fun setupView() {
        binding.btnLogout.setOnClickListener {
            logoutProcess()
        }
        checkAccountArtist()
    }

    private fun checkAccountArtist() {
        binding.imvVerified.isVisible = account.isArtist
        binding.tvDeleteAccount.isVisible = !account.isArtist
    }

    private fun logoutProcess() {
        viewModel.saveLoginState(false)
        viewModel.clearAccountState()

        val toAuthActivity = ProfileFragmentDirections.actionNavigationProfileToAuthActivity()
        toAuthActivity.username = account.username
        findNavController().navigate(toAuthActivity)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}