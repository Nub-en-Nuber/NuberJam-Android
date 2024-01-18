package com.example.nuberjam.ui.main.library.detail

import android.app.ActionBar.LayoutParams
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.nuberjam.R
import com.example.nuberjam.data.Result
import com.example.nuberjam.data.model.Music
import com.example.nuberjam.databinding.FavoriteStateButtonBinding
import com.example.nuberjam.databinding.FragmentDetailLibraryBinding
import com.example.nuberjam.ui.customview.CustomSnackbar
import com.example.nuberjam.ui.main.adapter.MusicAdapter
import com.example.nuberjam.ui.main.library.detail.deleteplaylist.DeletePlaylistDialogFragment
import com.example.nuberjam.ui.main.library.detail.editname.EditNameDialogFragment
import com.example.nuberjam.utils.BundleKeys
import com.example.nuberjam.utils.EditPhotoType
import com.example.nuberjam.utils.Helper
import com.example.nuberjam.utils.LibraryDetailType
import com.example.nuberjam.utils.extensions.collectLifecycleFlow
import com.example.nuberjam.utils.extensions.invisible
import com.example.nuberjam.utils.extensions.showNuberJamDefaultState
import com.example.nuberjam.utils.extensions.showNuberJamEmptyState
import com.example.nuberjam.utils.extensions.showNuberJamErrorState
import com.example.nuberjam.utils.extensions.showNuberJamLoadingState
import com.example.nuberjam.utils.extensions.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailLibraryFragment : Fragment() {

    private var _binding: FragmentDetailLibraryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailLibraryViewModel by viewModels()

    private lateinit var musicAdapter: MusicAdapter

    private var favoriteButtonBinding: FavoriteStateButtonBinding? = null

    companion object {
        const val EDIT_PLAYLIST_REQUEST_KEY = "edit_playlist_request_key"
        const val DELETE_PLAYLIST_REQUEST_KEY = "delete_playlist_request_key"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAppbar()
        setupRecyclerView()
        setupFragmentResultListener()
        initDataObserver()
    }

    private fun setupFragmentResultListener() {
        childFragmentManager.setFragmentResultListener(
            EDIT_PLAYLIST_REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            handleFragmentResultData(bundle)
        }

        setFragmentResultListener(
            EDIT_PLAYLIST_REQUEST_KEY,
        ) { _, bundle ->
            handleFragmentResultData(bundle)
        }

        childFragmentManager.setFragmentResultListener(
            DELETE_PLAYLIST_REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            val state = bundle.getBoolean(BundleKeys.DELETE_PLAYLIST_STATE_KEY)
            if (state) {
                findNavController().popBackStack()
            }
        }
    }

    private fun handleFragmentResultData(bundle: Bundle) {
        val state = bundle.getBoolean(BundleKeys.EDIT_PLAYLIST_STATE_KEY)
        if (state) {
            viewModel.getData()
        }
    }

    private fun setupAppbar() {
        val toolbar: Toolbar = binding.appbar.toolbar
        toolbar.navigationIcon = ContextCompat.getDrawable(
            requireContext(), R.drawable.ic_back_gray
        )
        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupRecyclerView() {
        musicAdapter = MusicAdapter(
            object : MusicAdapter.MusicAdapterCallback {
                override fun onItemClick(musicId: Int) {
                    navigateToDetailMusic(musicId)
                }

                override fun onAlbumImageClick(albumId: Int) {
                    goToDetailLibraryPage(LibraryDetailType.Album, albumId)
                }

                override fun onFavoriteActionClick(
                    musicId: Int,
                    isFavorite: Boolean,
                    buttonFavoriteState: FavoriteStateButtonBinding
                ) {
                    favoriteButtonBinding = buttonFavoriteState
                    if (isFavorite) {
                        viewModel.updateFavoriteData(musicId, false)
                    } else {
                        viewModel.updateFavoriteData(musicId, true)
                    }
                }

                override fun addItemToPlaylist(musicId: Int) {
                }
            },
            viewType = viewModel.libraryViewType,
            childFragmentManager = childFragmentManager
        )
        binding.rvMusicList.apply {
            adapter = musicAdapter
            layoutManager = LinearLayoutManager(requireActivity())
        }
    }

    private fun navigateToDetailMusic(musicId: Int) {
        val toMusicFragment =
            DetailLibraryFragmentDirections.actionDetailLibraryFragmentToMusicFragment()
        toMusicFragment.musicId = musicId
        findNavController().navigate(toMusicFragment)
    }

    private fun initDataObserver() {
        when (viewModel.libraryViewType) {
            LibraryDetailType.Favorite -> {
                binding.appbar.tvLibraryAppbar.text = getString(R.string.liked_song)
                viewLifecycleOwner.collectLifecycleFlow(viewModel.favoriteState) { result ->
                    when (result) {
                        is Result.Loading -> binding.msvPlaylistOuter.showNuberJamLoadingState()
                        is Result.Success -> {
                            val data = result.data
                            setViewState(getString(R.string.liked_song), data.size, null, data)
                        }

                        is Result.Error -> showErrorState(result.errorCode)
                        else -> {}
                    }
                }
            }

            LibraryDetailType.Album -> {
                binding.appbar.tvLibraryAppbar.text = getString(R.string.album)
                viewLifecycleOwner.collectLifecycleFlow(viewModel.albumState) { result ->
                    when (result) {
                        is Result.Loading -> binding.msvPlaylistOuter.showNuberJamLoadingState()
                        is Result.Success -> {
                            val data = result.data
                            setViewState(data.name, data.music?.size, data.photo, data.music)
                        }

                        is Result.Error -> showErrorState(result.errorCode)
                        else -> {}
                    }
                }
            }

            LibraryDetailType.Playlist -> {
                with(binding) {
                    val popupWindow = initPopupWindow()
                    appbar.tvLibraryAppbar.text = getString(R.string.playlist)
                    imvCover.imbKebab.visible()

                    imvCover.imbKebab.setOnClickListener {
                        popupWindow.showAsDropDown(it)
                    }

                    viewLifecycleOwner.collectLifecycleFlow(viewModel.playlistState) { result ->
                        when (result) {
                            is Result.Loading -> binding.msvPlaylistOuter.showNuberJamLoadingState()
                            is Result.Success -> {
                                val data = result.data
                                setViewState(
                                    data.info.name,
                                    data.music.size,
                                    data.info.photo,
                                    data.music
                                )

                                imvCover.ivGridImage.setOnClickListener {
                                    val toPhotoFragment =
                                        DetailLibraryFragmentDirections.actionDetailLibraryFragmentToEditPhotoFragment()
                                    toPhotoFragment.currentPhoto = data.info.photo
                                    toPhotoFragment.entryPoint = EditPhotoType.Playlist
                                    toPhotoFragment.playlistId = viewModel.playlistId
                                    findNavController().navigate(toPhotoFragment)
                                }
                            }

                            is Result.Error -> showErrorState(result.errorCode)
                            else -> {}
                        }
                    }
                }
            }
        }
        showSnackbarObserve()
        observeAddDeleteFavoriteState()
    }

    private fun initPopupWindow(): PopupWindow {
        val inflater: LayoutInflater =
            requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.popup_kebab_playlist, null)

        val popupWindow = PopupWindow(
            view,
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        val itemFirst = view.findViewById<RelativeLayout>(R.id.item_first)
        val tvFirst = view.findViewById<TextView>(R.id.tv_first)
        tvFirst.text = getString(R.string.edit)
        itemFirst.setOnClickListener {
            val editNameDialogFragment = EditNameDialogFragment.getInstance(
                viewModel.playlistId,
                binding.imvCover.tvLibraryTitle.text.toString()
            )
            editNameDialogFragment.show(childFragmentManager, EditNameDialogFragment.TAG)
            popupWindow.dismiss()
        }

        val itemSecond = view.findViewById<RelativeLayout>(R.id.item_second)
        val tvSecond = view.findViewById<TextView>(R.id.tv_second)
        tvSecond.text = getString(R.string.delete)
        itemSecond.setOnClickListener {
            DeletePlaylistDialogFragment.getInstance(viewModel.playlistId)
                .show(childFragmentManager, DeletePlaylistDialogFragment.TAG)
            popupWindow.dismiss()
        }

        return popupWindow
    }

    private fun setViewState(
        title: String?,
        dataSize: Int?,
        image: String?,
        listMusic: List<Music>?
    ) {
        with(binding) {
            val scale = requireContext().resources.displayMetrics.density
            val height = (166 * scale + 0.5f)
            msvPlaylistOuter.showNuberJamDefaultState()
            imvCover.cvPlaylistItem.layoutParams =
                ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, height.toInt())
            imvCover.tvLibraryTitle.text = title
            imvCover.tvLibraryType.text = getString(R.string.total_song, dataSize)
            if (image == null) {
                imvCover.ivGridImage.setImageResource(R.drawable.favorite_pic)
            } else {
                Glide.with(requireActivity()).load(image)
                    .placeholder(R.drawable.default_playlist)
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .error(R.drawable.default_playlist)
                    .into(imvCover.ivGridImage)
            }
            if (listMusic?.isEmpty() == true) {
                msvPlaylistInner.showNuberJamEmptyState(
                    lottieJson = null,
                    emptyMessage = getString(R.string.no_liked_song)
                )
            } else {
                musicAdapter.submitList(listMusic)
            }
        }
    }

    private fun showErrorState(errorCode: Int) {
        binding.msvPlaylistOuter.showNuberJamErrorState(
            errorMessage = Helper.getApiErrorMessage(
                requireActivity(),
                errorCode
            ),
            onButtonClicked = viewModel::getData
        )
        viewModel.setSnackbar(
            Helper.getApiErrorMessage(requireActivity(), errorCode),
            CustomSnackbar.STATE_ERROR
        )
    }

    private fun observeAddDeleteFavoriteState() {
        viewLifecycleOwner.collectLifecycleFlow(viewModel.addDeleteFavoriteState) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        updateFavoriteState(isLoading = true)
                    }

                    is Result.Success -> {
                        updateFavoriteState(isLoading = false, isSuccess = true)
                        viewModel.getData()
                    }

                    is Result.Error -> {
                        updateFavoriteState(isLoading = false, isSuccess = false)
                        viewModel.setSnackbar(
                            Helper.getApiErrorMessage(requireActivity(), result.errorCode),
                            CustomSnackbar.STATE_ERROR
                        )
                    }
                }
            }
        }
    }

    private fun updateFavoriteState(isLoading: Boolean, isSuccess: Boolean = false) {
        if (!isLoading) {
            favoriteButtonBinding?.loading?.invisible()
            val selectedView = favoriteButtonBinding?.imbLove
            selectedView?.visible()
            if (isSuccess) {
                selectedView?.setImageResource(
                    R.drawable.ic_love_red
                )
            } else {
                selectedView?.setImageResource(
                    R.drawable.ic_love_gray
                )
            }
        } else {
            favoriteButtonBinding?.loading?.visible()
            favoriteButtonBinding?.imbLove?.invisible()
        }
    }

    private fun showSnackbarObserve() {
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

    private fun goToDetailLibraryPage(viewType: LibraryDetailType, albumId: Int = 0) {
        val args = Bundle().apply {
            putSerializable(BundleKeys.LIBRARY_VIEW_TYPE_KEY, viewType)
            putInt(BundleKeys.ALBUM_ID_KEY, albumId)
        }
        findNavController().navigate(R.id.action_detailLibraryFragment_self, args)
    }
}