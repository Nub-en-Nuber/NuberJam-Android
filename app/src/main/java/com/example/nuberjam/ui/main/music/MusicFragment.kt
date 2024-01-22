package com.example.nuberjam.ui.main.music

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toolbar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.nuberjam.R
import com.example.nuberjam.data.Result
import com.example.nuberjam.data.model.Music
import com.example.nuberjam.databinding.FragmentMusicBinding
import com.example.nuberjam.service.MediaService
import com.example.nuberjam.ui.customview.CustomSnackbar
import com.example.nuberjam.ui.main.library.detail.searchplaylist.SearchPlaylistDialogFragment
import com.example.nuberjam.utils.Helper
import com.example.nuberjam.utils.extensions.collectLifecycleFlow
import com.example.nuberjam.utils.extensions.onlyVisibleIf
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MusicFragment : Fragment() {

    private var _binding: FragmentMusicBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MusicViewModel by viewModels()

    private lateinit var mediaService: MediaService
    private val seekbarProgressCycle = 500
    private var boundServiceStatus = false
    private var isMediaReady = false
    private var runnable: Runnable? = null
    private var handler: Handler = Handler(Looper.getMainLooper())

    private lateinit var musicData: Music

    private val connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName) {
            boundServiceStatus = false
        }

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val myBinder = service as MediaService.LocalBinder
            mediaService = myBinder.getService
            boundServiceStatus = true

            if (viewModel.musicId != mediaService.music?.id) {
                mediaService.changeMedia(viewModel.music!!)
            }
            setServiceConnection()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMusicBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun setToolbar() {
        val toolbar: Toolbar = binding.nowPlayingAppbar.toolbar
        toolbar.navigationIcon = ContextCompat.getDrawable(
            requireContext(), R.drawable.ic_back_gray
        )
        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.incShimmerLoading.shimmerMusic.startShimmerAnimation()
        if (runnable != null) handler.removeCallbacks(runnable!!)
        if (isMediaReady) {
            startProgressionSeekBar()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Helper.isAndroidTiramisuOrHigher()) {
            val permission = android.Manifest.permission.POST_NOTIFICATIONS
            requestPermissionLauncher.launch(permission)
        }

        setToolbar()
        showBottomBar(false)
        showSnackbarObserve()
        readDetailMusicObserve()
    }

    override fun onStop() {
        super.onStop()
        if (runnable != null) handler.removeCallbacks(runnable!!)
        binding.incMusicController.seekBar.progress = 0
        binding.incMusicController.tvCurrentDuration.text = Helper.displayDuration(0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        showBottomBar(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (boundServiceStatus) {
            requireActivity().unbindService(connection)
            boundServiceStatus = false
        }
    }

    private fun setServiceConnection() {
        mediaService.isReady.observe(viewLifecycleOwner) {
            isMediaReady = it
            showMediaLoading(!it)
            if (isMediaReady) {
                initSeekBar()
            } else {
                binding.incMusicController.seekBar.progress = 0
                if (runnable != null) handler.removeCallbacks(runnable!!)
            }
        }
        mediaService.isPlaying.observe(viewLifecycleOwner) {
            changeButtonView()
        }
    }

    private fun showMediaLoading(isMediaLoading: Boolean) {
        binding.incMusicController.mediaLoading.visibility =
            if (isMediaLoading) View.VISIBLE else View.GONE
    }

    private fun changeButtonView() {
        if (mediaService.mediaPlayer?.isPlaying == true) {
            binding.incMusicController.btnPlay.setImageDrawable(
                ContextCompat.getDrawable(
                    requireActivity(),
                    R.drawable.ic_pause_blue
                )
            )
        } else {
            binding.incMusicController.btnPlay.setImageDrawable(
                ContextCompat.getDrawable(
                    requireActivity(),
                    R.drawable.ic_play_blue
                )
            )
        }
    }

    private fun showBottomBar(isVisible: Boolean) {
        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        navBar.visibility = if (isVisible) View.VISIBLE else View.GONE

        val currentMusic = requireActivity().findViewById<View>(R.id.current_playing_layout)
        currentMusic.onlyVisibleIf { isVisible }
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

    private fun readDetailMusicObserve() {
        viewLifecycleOwner.collectLifecycleFlow(viewModel.musicState) { result ->
            showLoading(result is Result.Loading)
            when (result) {
                is Result.Success -> setView(result.data)
                is Result.Error -> viewModel.setSnackbar(
                    Helper.getApiErrorMessage(requireActivity(), result.errorCode),
                    CustomSnackbar.STATE_ERROR
                )

                else -> Unit
            }
        }
    }

    private fun setView(music: Music) {
        viewModel.music = music

        Glide.with(this).load(music.albumPhoto).into(binding.ivAlbumImage)
        binding.incMusicController.tvSongTitle.text = music.name
        binding.incMusicController.tvArtist.text =
            Helper.concatenateArtist(music.artist ?: ArrayList())
        binding.incMusicController.tvDuration.text = Helper.displayDuration(music.duration ?: 0)
        binding.incMusicController.tvCurrentDuration.text = Helper.displayDuration(0)
        binding.incMusicController.btnPlay.setOnClickListener {
            if (isMediaReady) {
                mediaService.playOrPauseMedia()
            }
        }

        setMediaService(music)
    }

    private fun setMediaService(music: Music) {
        val mediaIntent = Intent(requireActivity(), MediaService::class.java)

        val isServiceRunning =
            Helper.isServiceRunning(requireActivity(), MediaService::class.java.name)

        if (!isServiceRunning) {
            showMediaLoading(true)
            mediaIntent.putExtra(MediaService.EXTRA_MEDIA_FILE, music)
            requireActivity().startService(mediaIntent)
        }

        requireActivity().bindService(mediaIntent, connection, 0)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.content.visibility = View.GONE
            binding.incShimmerLoading.shimmerMusic.visibility = View.VISIBLE
        } else {
            binding.content.visibility = View.VISIBLE
            binding.incShimmerLoading.shimmerMusic.visibility = View.GONE
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // PERMISSION GRANTED
        } else {
            findNavController().popBackStack()
        }
    }

    private fun initSeekBar() {
        with(binding.incMusicController.seekBar) {
            max = (mediaService.mediaPlayer?.duration ?: 0) / seekbarProgressCycle
            setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                    if (b) mediaService.seekMedia(i * seekbarProgressCycle)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                }
            })
            startProgressionSeekBar()
        }
    }

    private fun startProgressionSeekBar() {
        runnable = Runnable {
            val currentDuration = mediaService.mediaPlayer?.currentPosition ?: 0
            binding.incMusicController.seekBar.progress = currentDuration / seekbarProgressCycle
            binding.incMusicController.tvCurrentDuration.text =
                Helper.displayDuration(currentDuration / 1000)

            if (runnable != null) handler.postDelayed(
                runnable!!, (seekbarProgressCycle / 2).toLong()
            )
        }
        handler.postDelayed(runnable!!, (seekbarProgressCycle / 2).toLong())
    }
}