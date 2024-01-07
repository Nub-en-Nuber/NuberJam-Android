package com.example.nuberjam.ui.main.profile.editphoto

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.nuberjam.R
import com.example.nuberjam.data.Result
import com.example.nuberjam.databinding.FragmentEditPhotoBinding
import com.example.nuberjam.ui.customview.CustomSnackbar
import com.example.nuberjam.ui.main.profile.ProfileFragment
import com.example.nuberjam.utils.BundleKeys
import com.example.nuberjam.utils.Helper
import com.example.nuberjam.utils.PhotoLoaderManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class EditPhotoFragment : Fragment() {

    private var _binding: FragmentEditPhotoBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EditPhotoViewModel by viewModels()

    private lateinit var tempUri: Uri
    private lateinit var currentPhoto: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditPhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setArgs()
        initUI()
        setToolbar()
        initAction()
        initObserver()
    }

    private fun setArgs() {
        val args: EditPhotoFragmentArgs by navArgs()
        currentPhoto = args.currentPhoto
    }

    private fun initUI() {
        Glide.with(requireActivity()).load(currentPhoto)
            .placeholder(R.drawable.ic_profile_placeholder)
            .error(R.drawable.ic_profile_placeholder)
            .apply(RequestOptions.skipMemoryCacheOf(true))
            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
            .into(binding.imvProfilePhoto)
    }

    private fun setToolbar() {
        val toolbar: Toolbar = binding.appbar.toolbar
        toolbar.navigationIcon = ContextCompat.getDrawable(
            requireContext(), R.drawable.ic_back_gray
        )
        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initAction() {
        with(binding) {
            imbGallery.setOnClickListener { actionOpenGallery() }
            imbPhoto.setOnClickListener { actionOpenCamera() }
            btnSave.setOnClickListener {
                uploadPhoto()
            }
            btnCancel.setOnClickListener {
                findNavController().popBackStack()
            }
        }
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
        val chosenImage = getChosenImage()
        if (chosenImage != null)
            viewModel.updateAccount(chosenImage)
    }

    private fun getChosenImage(): File? {
        var currentFile: File? = null
        viewModel.currentImageUri?.let { uri ->
            currentFile = PhotoLoaderManager.uriToFile(uri, requireActivity())
        }
        return currentFile
    }

    private fun initObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.updatePhotoState.collect { result ->
                    if (result != null) {
                        showLoading(result is Result.Loading)
                        when (result) {
                            is Result.Success -> {
                                val chosenImage = getChosenImage()
                                if (chosenImage != null)
                                    PhotoLoaderManager.deleteFile(chosenImage)
                                setFragmentResult(
                                    ProfileFragment.EDIT_REQUEST_KEY,
                                    bundleOf(BundleKeys.EDIT_PROFILE_STATE_KEY to true)
                                )
                                findNavController().popBackStack()
                            }

                            is Result.Error -> {
                                showError(result.errorCode)
                            }

                            else -> {}
                        }
                    }
                }
            }
        }

        viewModel.snackbarState.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { snackbarState ->
                val customSnackbar =
                    CustomSnackbar.build(layoutInflater, binding.root, snackbarState.length)
                customSnackbar.setMessage(snackbarState.message)
                customSnackbar.setState(snackbarState.state)
                customSnackbar.show()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loading.isInvisible = !isLoading
        binding.btnSave.isInvisible = isLoading
        binding.btnCancel.isInvisible = isLoading
    }

    private fun showError(errorCode: Int) {
        val message = Helper.getApiErrorMessage(requireActivity(), errorCode)
        viewModel.setSnackbar(
            message,
            CustomSnackbar.STATE_SUCCESS
        )
    }
}