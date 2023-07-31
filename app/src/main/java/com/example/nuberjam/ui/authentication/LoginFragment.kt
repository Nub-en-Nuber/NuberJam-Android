package com.example.nuberjam.ui.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.nuberjam.R
import com.example.nuberjam.databinding.FragmentHomeBinding
import com.example.nuberjam.data.Result
import com.example.nuberjam.databinding.FragmentLoginBinding
import com.example.nuberjam.ui.ViewModelFactory
import com.example.nuberjam.ui.customview.CustomSnackbar


class LoginFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
        val viewModels: AuthViewModel by viewModels {
            factory
        }
        viewModel = viewModels

        binding.btnLogin.setOnClickListener {
            makeLogin()
        }
        binding.btnRegister.setOnClickListener {
            moveToRegister()
        }
    }

    private fun moveToRegister() {
//        TODO("Not yet implemented")
    }

    private fun makeLogin() {
        val usernameOrEmail = binding.etUsernameEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        makeLoginObserve(usernameOrEmail, password)
    }

    private fun makeLoginObserve(usernameOrEmail: String, password: String) {
        viewModel.makeLogin(usernameOrEmail, password).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> showLoading(true)
                    is Result.Success -> {
                        showLoading(false)
                        val isLoginSuccess = result.data
                        if (isLoginSuccess) saveSession(usernameOrEmail)
                        else showSnackbar(
                            getString(R.string.login_failed_message),
                            CustomSnackbar.STATE_ERROR
                        )
                    }
                    is Result.Error -> {
                        showLoading(false)
                        showSnackbar(result.error, CustomSnackbar.STATE_ERROR)
                    }
                }
            }
        }
    }

    private fun saveSession(usernameOrEmail: String) {
//        TODO("Not yet implemented")
        val username = "Test"
        showSnackbar(
            getString(R.string.login_success_message, username),
            CustomSnackbar.STATE_SUCCESS
        )
        findNavController().navigate(R.id.action_navigation_login_tomainActivity)
        requireActivity().finish()
    }

    private fun showSnackbar(
        message: String,
        state: Int,
        length: Int = CustomSnackbar.LENGTH_LONG
    ) {
        val customSnackbar =
            CustomSnackbar.build(layoutInflater, binding.root, length)
        customSnackbar.setMessage(message)
        customSnackbar.setState(state)
        customSnackbar.show()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.loading.linearLoading.visibility = View.VISIBLE
        } else {
            binding.loading.linearLoading.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}