package com.example.nuberjam.ui.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.nuberjam.R
import com.example.nuberjam.data.model.Account
import com.example.nuberjam.databinding.FragmentRegisterBinding
import com.example.nuberjam.ui.ViewModelFactory
import com.example.nuberjam.data.Result
import com.example.nuberjam.ui.customview.CustomSnackbar


class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AuthViewModel

    companion object {
        const val REGISTER_SUCCESS_KEY = "register_success_key"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbar()

        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
        val initViewModel: AuthViewModel by viewModels {
            factory
        }
        viewModel = initViewModel

        binding.btnRegister.setOnClickListener {
            makeRegister()
        }
    }

    private fun setToolbar() {
        val toolbar: Toolbar = binding.registerAppbar.toolbar
        toolbar.navigationIcon = ContextCompat.getDrawable(
            requireContext(),
            R.drawable.ic_back_gray
        )
        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun makeRegister() {
        val name = binding.etName.text.toString().trim()
        val username = binding.etUsername.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        val account = Account(
            name = name,
            username = username,
            email = email,
            password = password,
            id = 0,
            photo = "null"
        )
        makeRegisterObserve(account)
    }

    private fun makeRegisterObserve(account: Account) {
        viewModel.makeRegister(account).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> showLoading(true)
                    is Result.Success -> {
                        showLoading(false)

                        val isRegisterSuccess = result.data
                        if (isRegisterSuccess) {
                            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                                REGISTER_SUCCESS_KEY, isRegisterSuccess
                            )
                            findNavController().popBackStack()
                        } else
                            showSnackbar(
                                getString(R.string.register_failed_message),
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

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.loading.linearLoading.visibility = View.VISIBLE
        } else {
            binding.loading.linearLoading.visibility = View.GONE
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
}