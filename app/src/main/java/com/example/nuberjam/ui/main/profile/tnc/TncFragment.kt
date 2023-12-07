package com.example.nuberjam.ui.main.profile.tnc

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.nuberjam.R
import com.example.nuberjam.databinding.FragmentRegisterBinding
import com.example.nuberjam.databinding.FragmentTncBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class TncFragment : Fragment() {

    private var _binding: FragmentTncBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
    _binding = FragmentTncBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar()
        setTncData()
    }

    private fun setToolbar() {
        val toolbar: Toolbar = binding.tncAppbar.toolbar
        toolbar.navigationIcon = ContextCompat.getDrawable(
            requireContext(), R.drawable.ic_back_gray
        )
        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setTncData() {
        binding.tncBody.tvIntroduction.text = getString(R.string.text_introduction)
        binding.tncBody.tvAcceptance.text = getString(R.string.text_acceptance)
        binding.tncBody.tvPrivacyPolicy.text = getString(R.string.text_privacy_policy)
        binding.tncBody.tvPersonalInformation.text = getString(R.string.text_personal_information)
        binding.tncBody.tvPersonalData.text = getString(R.string.text_personal_data)
        binding.tncBody.tvSecurityPersonalInformation.text = getString(R.string.text_personal_information)
        binding.tncBody.tvCopyrightIp.text = getString(R.string.text_copyright_ip)
        binding.tncBody.tvProductService.text = getString(R.string.text_product_service)
        binding.tncBody.tvTakedown.text = getString(R.string.text_takedown)
        binding.tncBody.tvLimitation.text = getString(R.string.text_limitation)
        binding.tncBody.tvContactInformation.text = getString(R.string.text_contact_information)
        binding.tncBody.tvUpdateNotification.text = getString(R.string.text_update_notification)
    }
}