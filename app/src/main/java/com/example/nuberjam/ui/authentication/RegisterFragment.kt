package com.example.nuberjam.ui.authentication

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.nuberjam.R
import com.example.nuberjam.data.model.Account
import com.example.nuberjam.databinding.FragmentRegisterBinding
import com.example.nuberjam.ui.ViewModelFactory
import com.example.nuberjam.data.Result
import com.example.nuberjam.ui.customview.CustomSnackbar
import com.example.nuberjam.utils.FormValidation


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

        setFormState()
        binding.btnRegister.setOnClickListener {
            makeRegister()
        }
    }

    private fun setFormState() {
        setFormNameListener()
        setFormUsernameListener()
        setFormEmailListener()
        setFormPasswordListener()
        setFormConfirmPasswordListener()
    }

    private fun setFormConfirmPasswordListener() {
        binding.etConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                formConfirmPasswordProcess(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                formConfirmPasswordProcess(s.toString())
            }
        })
    }

    private fun formConfirmPasswordProcess(text: String) {
        viewModel.formConfirmPassword = text
        if (viewModel.formConfirmPassword.isEmpty()) {
            viewModel.formConfirmPasswordValid = false
            binding.etConfirmPassword.error = getString(R.string.form_empty_message)
        } else if (!FormValidation.isPasswordSame(
                viewModel.formPassword,
                viewModel.formConfirmPassword
            )
        ) {
            viewModel.formConfirmPasswordValid = false
            binding.etConfirmPassword.error = getString(R.string.form_password_not_same)
        } else {
            viewModel.formConfirmPasswordValid = true
            binding.etConfirmPassword.error = null
        }
    }

    private fun setFormPasswordListener() {
        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                formPasswordProcess(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                formPasswordProcess(s.toString())
            }
        })
    }

    private fun formPasswordProcess(text: String) {
        viewModel.formPassword = text
        if (viewModel.formPassword.isEmpty()) {
            binding.etPassword.error = getString(R.string.form_empty_message)
        } else {
            binding.etPassword.error = null
        }
        viewModel.formPasswordValid = checkPasswordRequirements(viewModel.formPassword)
    }

    private fun setFormEmailListener() {
        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                formEmailProcess(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                formEmailProcess(s.toString())
            }
        })
    }

    private fun formEmailProcess(text: String) {
        viewModel.formEmail = text
        if (viewModel.formEmail.isEmpty()) {
            viewModel.formEmailValid = false
            binding.etEmail.error = getString(R.string.form_empty_message)
        } else if (!FormValidation.isEmailValid(viewModel.formEmail)) {
            viewModel.formEmailValid = false
            binding.etEmail.error = getString(R.string.form_email_incorrect_format)
        } else {
            viewModel.checkEmailExist(viewModel.formEmail).observe(viewLifecycleOwner) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> viewModel.formEmailValid = false
                        is Result.Success -> {
                            val isEmailExist = result.data
                            if (isEmailExist) {
                                viewModel.formEmailValid = false
                                binding.etEmail.error =
                                    getString(R.string.form_email_used)
                            } else {
                                viewModel.formEmailValid = true
                                binding.etEmail.error = null
                            }
                        }
                        is Result.Error -> {
                            viewModel.formEmailValid = false
                            binding.etEmail.error = result.error
                        }
                    }
                }

            }
        }
    }

    private fun setFormUsernameListener() {
        binding.etUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                formUsernameProcess(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                formUsernameProcess(s.toString())
            }
        })
    }

    private fun formUsernameProcess(text: String) {
        viewModel.formUsername = text
        if (viewModel.formUsername.isEmpty()) {
            viewModel.formUsernameValid = false
            binding.etUsername.error = getString(R.string.form_empty_message)
        } else if (!FormValidation.isUsernameValid(viewModel.formUsername)) {
            viewModel.formUsernameValid = false
            binding.etUsername.error = getString(R.string.form_username_contain_spaces)
        } else {
            viewModel.checkUsernameExist(viewModel.formUsername)
                .observe(viewLifecycleOwner) { result ->
                    if (result != null) {
                        when (result) {
                            is Result.Loading -> viewModel.formUsernameValid = false
                            is Result.Success -> {
                                val isUsernameExist = result.data
                                if (isUsernameExist) {
                                    viewModel.formUsernameValid = false
                                    binding.etUsername.error =
                                        getString(R.string.form_username_used)
                                } else {
                                    viewModel.formUsernameValid = true
                                    binding.etUsername.error = null
                                }
                            }
                            is Result.Error -> {
                                viewModel.formUsernameValid = false
                                binding.etUsername.error = result.error
                            }
                        }
                    }
                }
        }
    }

    private fun setFormNameListener() {
        binding.etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                formNameProcess(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                formNameProcess(s.toString())
            }

        })
    }

    private fun formNameProcess(text: String) {
        viewModel.formName = text
        if (viewModel.formName.isEmpty()) {
            viewModel.formNameValid = false
            binding.etName.error = getString(R.string.form_empty_message)
        } else {
            viewModel.formNameValid = true
            binding.etName.error = null
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
        if (viewModel.checkFormRegisterValid()) {
            val account = Account(
                name = viewModel.formName,
                username = viewModel.formUsername,
                email = viewModel.formEmail,
                password = viewModel.formPassword,
                id = 0,
                photo = "null"
            )
            makeRegisterObserve(account)
        } else {
            showSnackbar(getString(R.string.register_failed_message), CustomSnackbar.STATE_ERROR)
        }
    }

    private fun checkPasswordRequirements(password: String): Boolean {
        var isPasswordValid = true

        val passwordRequirements = FormValidation.isPasswordValid(password)
        if (passwordRequirements[FormValidation.KEY_MINIMAL_CHARACTER] == true) {
            binding.passwordReq.ivMinPassword.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_checklist_green
                )
            )
        } else {
            isPasswordValid = false
            binding.passwordReq.ivMinPassword.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_clause_red
                )
            )
        }

        if (passwordRequirements[FormValidation.KEY_CONTAIN_NUMBER] == true) {
            binding.passwordReq.ivNumbers.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_checklist_green
                )
            )
        } else {
            isPasswordValid = false
            binding.passwordReq.ivNumbers.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_clause_red
                )
            )
        }

        if (passwordRequirements[FormValidation.KEY_CONTAIN_UPPER] == true) {
            binding.passwordReq.ivUppercase.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_checklist_green
                )
            )
        } else {
            isPasswordValid = false
            binding.passwordReq.ivUppercase.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_clause_red
                )
            )
        }

        if (passwordRequirements[FormValidation.KEY_CONTAIN_LOWER] == true) {
            binding.passwordReq.ivLowercase.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_checklist_green
                )
            )
        } else {
            isPasswordValid = false
            binding.passwordReq.ivLowercase.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_clause_red
                )
            )
        }

        return isPasswordValid
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}