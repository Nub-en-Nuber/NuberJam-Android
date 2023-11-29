package com.example.nuberjam.ui.main.profile.editphoto

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.example.nuberjam.R
import com.example.nuberjam.databinding.FragmentEditPhotoBinding
import com.example.nuberjam.databinding.FragmentProfileBinding
import com.example.nuberjam.ui.main.profile.ProfileViewModel
import com.example.nuberjam.utils.PhotoLoaderManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditPhotoFragment : Fragment() {

    private var _binding: FragmentEditPhotoBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EditPhotoViewModel by viewModels()

    private lateinit var tempUri: Uri

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditPhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAction()
    }

    private fun initAction() {
        binding.imbGallery.setOnClickListener { actionOpenGallery() }
        binding.imbPhoto.setOnClickListener { actionOpenCamera() }
    }

    private fun actionOpenCamera() {
        tempUri = PhotoLoaderManager.buildNewUri(requireActivity())
        cameraIntentLauncher.launch(tempUri)
    }

    private val cameraIntentLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            viewModel.currentImageUri = tempUri
            showImage()
        }
    }

    private fun actionOpenGallery() {
        pickMediaLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val pickMediaLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                viewModel.currentImageUri = uri
                showImage()
            }
        }

    private fun showImage() {
        viewModel.currentImageUri?.let { uri ->
            binding.imvProfilePhoto.setImageURI(uri)
        }
    }

    private fun uploadPhoto() {
        viewModel.currentImageUri?.let { uri ->
            val file = PhotoLoaderManager.uriToFile(uri, requireActivity())
            /*
            TODO: UPLOAD WITH FILE
            TODO: DON'T FORGET TO DELETE FILE USING PhotoLoaderManager.deleteFile(file) AFTER UPLOAD SUCCESS
            CODE:
            if (file != null) {
                PhotoLoaderManager.deleteFile(file)
            }
             */

        }
    }
}