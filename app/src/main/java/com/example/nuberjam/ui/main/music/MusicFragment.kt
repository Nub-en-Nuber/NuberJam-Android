package com.example.nuberjam.ui.main.music

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
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
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.nuberjam.R
import com.example.nuberjam.data.Result
import com.example.nuberjam.data.model.Music
import com.example.nuberjam.databinding.FragmentMusicBinding
import com.example.nuberjam.service.MediaService
import com.example.nuberjam.ui.customview.CustomSnackbar
import com.example.nuberjam.utils.Helper
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

    private val connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName) {
            boundServiceStatus = false
        }

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val myBinder = service as MediaService.LocalBinder
            mediaService = myBinder.getService
            boundServiceStatus = true

            setServiceData()
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
        showNavBar(false)
        setArgs()
        showSnackbarObserve()
        setData()
    }

    override fun onStop() {
        super.onStop()
        if (runnable != null) handler.removeCallbacks(runnable!!)
        binding.incMusicController.seekBar.progress = 0
    }

    override fun onDestroyView() {
        super.onDestroyView()
        showNavBar(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (boundServiceStatus) {
            requireActivity().unbindService(connection)
            boundServiceStatus = false
        }
    }

    private fun setServiceData() {
        mediaService.isReady.observe(viewLifecycleOwner) {
            isMediaReady = it
            if (isMediaReady) {
                showMediaLoading(false)
                viewModel.saveCurrentMusic()
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

    private fun showNavBar(isVisible: Boolean) {
        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        navBar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    private fun setArgs() {
        val args: MusicFragmentArgs by navArgs()
        viewModel.musicId = args.musicId
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

    private fun setData() {
        viewModel.getCurrentMusic().observe(viewLifecycleOwner) { id ->
            viewModel.currentPlayingMusicId = id
        }
        viewModel.getAccountState().observe(viewLifecycleOwner) { account ->
            if (account != null) {
                if (viewModel.accountId == null) {
                    viewModel.accountId = account.id
                    readDetailMusicObserve()
                }
            }
        }
    }

    private fun readDetailMusicObserve() {
        viewModel.readDetailMusic()?.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }

                    is Result.Success -> {
                        showLoading(false)
                        val musicData = result.data
                        setView(musicData)
                    }

                    is Result.Error -> {
                        showLoading(false)
                        viewModel.setSnackbar(
                            Helper.getApiErrorMessage(requireActivity(), result.errorCode),
                            CustomSnackbar.STATE_ERROR
                        )
                    }
                }
            }
        }
    }

    private fun setView(music: Music) {
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
        if (isServiceRunning(MediaService::class.java.name) && viewModel.isMusicIdSameCurrentPlaying()) {
            resumeMediaView()
        } else if (!boundServiceStatus || !isMediaReady) {
            showMediaLoading(true)
            initMediaService(music)
        }
    }

    private fun resumeMediaView() {
        val mediaIntent = Intent(requireActivity(), MediaService::class.java)
        requireActivity().bindService(mediaIntent, connection, Context.BIND_AUTO_CREATE)
    }

    @Suppress("DEPRECATION")
    private fun isServiceRunning(serviceClassName: String): Boolean {
        val activityManager =
            requireActivity().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val services: List<ActivityManager.RunningServiceInfo> =
            activityManager.getRunningServices(Int.MAX_VALUE)
        for (runningServiceInfo in services) {
            if (runningServiceInfo.service.className == serviceClassName) {
                return true
            }
        }
        return false
    }

    private fun initMediaService(music: Music) {
        val mediaIntent = Intent(requireActivity(), MediaService::class.java)
        requireActivity().stopService(mediaIntent)
        mediaIntent.putExtra(MediaService.EXTRA_MEDIA_FILE, music)
        requireActivity().startService(mediaIntent)
        requireActivity().bindService(mediaIntent, connection, Context.BIND_AUTO_CREATE)
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
        binding.incMusicController.seekBar.max =
            (mediaService.mediaPlayer?.duration ?: 0) / seekbarProgressCycle
        binding.incMusicController.seekBar.setOnSeekBarChangeListener(object :
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