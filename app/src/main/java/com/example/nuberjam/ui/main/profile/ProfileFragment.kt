package com.example.nuberjam.ui.main.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.nuberjam.databinding.FragmentProfileBinding
import com.example.nuberjam.ui.ViewModelFactory
import com.example.nuberjam.ui.authentication.AuthActivity

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ProfileViewModel

    companion object {
        const val LOGOUT_SUCCESS_EXTRA = "logout_success_extra"
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

        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
        val initViewModel: ProfileViewModel by viewModels {
            factory
        }
        viewModel = initViewModel

        binding.btnLogout.setOnClickListener {
            logoutProcess()
        }
    }

    private fun logoutProcess() {
        viewModel.getAccountState().observe(viewLifecycleOwner) { account ->
            viewModel.saveLoginState(false)
            viewModel.clearAccountState()

            val toAuthActivity = ProfileFragmentDirections.actionNavigationProfileToAuthActivity()
            toAuthActivity.username = account.username
            findNavController().navigate(toAuthActivity)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}