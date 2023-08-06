package com.example.nuberjam.ui.authentication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.nuberjam.R
import com.example.nuberjam.data.Result
import com.example.nuberjam.databinding.FragmentLoginBinding
import com.example.nuberjam.ui.ViewModelFactory
import com.example.nuberjam.ui.authentication.LoginFragmentDirections.ActionNavigationLoginToMainActivity
import com.example.nuberjam.ui.customview.CustomSnackbar
import com.example.nuberjam.ui.main.MainActivity

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AuthViewModel

    companion object {
        val TAG: String = LoginFragment::class.java.simpleName
        const val LOGIN_SUCCESS_EXTRA = "login_success_extra"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadNavigationData()

        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
        val initViewModel: AuthViewModel by viewModels {
            factory
        }
        viewModel = initViewModel

        binding.btnLogin.setOnClickListener {
            makeLogin()
        }
        binding.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_login_to_navigation_register)
        }
    }

    private fun loadNavigationData() {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(
            RegisterFragment.REGISTER_SUCCESS_KEY
        )
            ?.observe(viewLifecycleOwner) { isRegisterSuccess ->
                if (isRegisterSuccess) {
                    showSnackbar(
                        getString(R.string.register_success_message),
                        CustomSnackbar.STATE_SUCCESS
                    )
                }
            }
    }

    private fun makeLogin() {
        var isError = false
        val usernameOrEmail = binding.etUsernameEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (usernameOrEmail.isEmpty()) {
            isError = true
            binding.etUsernameEmail.error = getString(R.string.form_empty_message)
        }
        if (password.isEmpty()) {
            isError = true
            binding.etPassword.error = getString(R.string.form_empty_message)
        }

        if (!isError) {
            makeLoginObserve(usernameOrEmail, password)
        }
    }

    private fun makeLoginObserve(usernameOrEmail: String, password: String) {
        viewModel.makeLogin(usernameOrEmail, password).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> showLoading(true)
                    is Result.Success -> {
                        val isLoginSuccess = result.data
                        if (isLoginSuccess) saveSession(usernameOrEmail)
                        else {
                            showLoading(false)
                            showSnackbar(
                                getString(R.string.login_failed_message),
                                CustomSnackbar.STATE_ERROR
                            )
                        }
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
        getAccountDataObserve(usernameOrEmail)
    }

    private fun getAccountDataObserve(usernameOrEmail: String) {
        viewModel.getAccountData(usernameOrEmail).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> showLoading(true)
                    is Result.Success -> {
                        showLoading(false)

                        val account = result.data
                        viewModel.saveAccountState(account)
                        viewModel.saveLoginState(true)

                        val toMainActivity = LoginFragmentDirections.actionNavigationLoginToMainActivity()
                        toMainActivity.username = account.username
                        findNavController().navigate(toMainActivity)
                    }
                    is Result.Error -> {
                        showLoading(false)
                        showSnackbar(result.error, CustomSnackbar.STATE_ERROR)
                    }
                }
            }
        }
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