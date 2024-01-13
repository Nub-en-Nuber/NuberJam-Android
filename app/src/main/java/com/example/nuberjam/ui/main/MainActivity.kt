package com.example.nuberjam.ui.main

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.nuberjam.R
import com.example.nuberjam.data.Result
import com.example.nuberjam.data.model.Music
import com.example.nuberjam.databinding.ActivityMainBinding
import com.example.nuberjam.service.MediaService
import com.example.nuberjam.ui.customview.CustomSnackbar
import com.example.nuberjam.ui.main.home.HomeFragment
import com.example.nuberjam.ui.main.home.HomeFragmentDirections
import com.example.nuberjam.ui.main.library.detail.DetailLibraryFragment
import com.example.nuberjam.ui.main.library.detail.DetailLibraryFragmentDirections
import com.example.nuberjam.utils.Helper
import com.example.nuberjam.utils.extensions.collectLifecycleFlow
import com.example.nuberjam.utils.extensions.onlyVisibleIf
import com.example.nuberjam.utils.extensions.visibleIf
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    lateinit var navController: NavController
    private var doSnackbar = true

    companion object {
        private const val STATE_SNACKBAR = "state_snackbar"
    }

    private var isCurrentMusicError = false
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

            setServiceConnection()
        }
    }

    override fun onResume() {
        super.onResume()
        if (runnable != null) handler.removeCallbacks(runnable!!)
        if (isMediaReady) {
            startProgressionSeekBar()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            doSnackbar = savedInstanceState.getBoolean(STATE_SNACKBAR, true)
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadNavigationData()

        val navView: BottomNavigationView = binding.navView

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        navView.setupWithNavController(navController)

        showSnackbarObserve()
        observeMusicState()
    }

    private fun bindMediaService() {
        val mediaIntent = Intent(this, MediaService::class.java)
        bindService(mediaIntent, connection, 0)
    }

    private fun setServiceConnection() {
        mediaService.isReady.observe(this) {
            isMediaReady = it
            if (isMediaReady) {
                binding.currentPlayingLayout.mediaLoading.visibleIf { false }
                binding.currentPlayingLayout.seekBar.visibleIf { true }
                initSeekBar()
            } else {
                binding.currentPlayingLayout.seekBar.progress = 0
                if (runnable != null) handler.removeCallbacks(runnable!!)
            }
        }
        mediaService.musicLiveData.observe(this) {
            setCurrentMusicView(it)
        }
        mediaService.isPlaying.observe(this) {
            changeButtonView()
        }
    }

    private fun changeButtonView() {
        if (mediaService.mediaPlayer?.isPlaying == true) {
            binding.currentPlayingLayout.btnPlay.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_pause_blue
                )
            )
        } else {
            binding.currentPlayingLayout.btnPlay.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_play_blue
                )
            )
        }
    }

    private fun initSeekBar() {
        with(binding.currentPlayingLayout.seekBar) {
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
            binding.currentPlayingLayout.seekBar.progress = currentDuration / seekbarProgressCycle

            if (runnable != null) handler.postDelayed(
                runnable!!, (seekbarProgressCycle / 2).toLong()
            )
        }
        handler.postDelayed(runnable!!, (seekbarProgressCycle / 2).toLong())
    }

    private fun observeMusicState() {
        collectLifecycleFlow(viewModel.musicState) { result ->
            binding.currentPlayingLayout.container.onlyVisibleIf { viewModel.currentMusic != null }
            when (result) {
                is Result.Success -> setCurrentMusicView(result.data)
                is Result.Error -> setCurrentMusicView(null)
                else -> Unit
            }
        }
    }

    private fun navigateToDetailMusic(musicId: Int) {
        val fragments = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        val musicFragmentDirection = when (fragments?.childFragmentManager?.fragments?.get(0)) {
            is HomeFragment -> HomeFragmentDirections
                .actionNavigationHomeToMusicFragment().apply {
                    this.musicId = musicId
                }

            is DetailLibraryFragment -> DetailLibraryFragmentDirections
                .actionDetailLibraryFragmentToMusicFragment().apply {
                    this.musicId = musicId
                }

            else -> null
        }

        if (musicFragmentDirection != null) {
            navController.navigate(musicFragmentDirection)
        }
    }

    private fun setCurrentMusicView(music: Music?) {
        binding.currentPlayingLayout.container.setOnClickListener {
            if (music?.id != null) navigateToDetailMusic(music.id)
        }

        if (!boundServiceStatus && viewModel.currentMusic != null) {
            bindMediaService()
        }

        if (music != null) {
            isCurrentMusicError = false
            with(binding.currentPlayingLayout) {
                Glide.with(applicationContext).load(music.albumPhoto).into(imvAlbum)
                tvTitle.text = music.name
                tvSinger.text = music.artist?.let { Helper.concatenateArtist(it) }
                seekBar.max = music.duration!! * 1000
                seekBar.progress = viewModel.currentMusic!!.progress

                btnPlay.setImageResource(R.drawable.ic_play_blue)
                val isServiceRunning =
                    Helper.isServiceRunning(this@MainActivity, MediaService::class.java.name)
                btnPlay.setOnClickListener {
                    if (!isServiceRunning) {
                        initMediaService(music)
                    } else {
                        mediaService.playOrPauseMedia()
                    }
                }
            }
        } else {
            isCurrentMusicError = true
            with(binding.currentPlayingLayout) {
                imvAlbum.setImageResource(R.drawable.ic_profile_placeholder)
                tvTitle.text = getString(R.string.error)
                tvSinger.text = getString(R.string.error)
                seekBar.max = 0
                seekBar.progress = 0

                btnPlay.setImageResource(R.drawable.ic_refresh)
                btnPlay.setOnClickListener {
                    viewModel.refreshMusicState()
                }
            }
        }
    }

    private fun initMediaService(music: Music) {
        binding.currentPlayingLayout.mediaLoading.visibleIf { true }
        binding.currentPlayingLayout.seekBar.visibleIf { false }
        val mediaIntent = Intent(this, MediaService::class.java)
        mediaIntent.putExtra(MediaService.EXTRA_MEDIA_FILE, music)
        startService(mediaIntent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(STATE_SNACKBAR, false)
    }

    override fun onStop() {
        super.onStop()
        if (runnable != null) handler.removeCallbacks(runnable!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(connection)
        stopService(Intent(this, MediaService::class.java))
    }

    private fun loadNavigationData() {
        if (intent != null) {
            val username = intent.extras?.let { MainActivityArgs.fromBundle(it).username }
            if (username != null && doSnackbar) viewModel.setSnackbar(
                getString(R.string.login_success_message, username), CustomSnackbar.STATE_SUCCESS
            )
        }
    }

    private fun showSnackbarObserve() {
        viewModel.snackbarState.observe(this) { event ->
            event.getContentIfNotHandled()?.let { snackbarState ->
                val customSnackbar =
                    CustomSnackbar.build(layoutInflater, binding.root, snackbarState.length)
                customSnackbar.setMessage(snackbarState.message)
                customSnackbar.setState(snackbarState.state)
                customSnackbar.show()
            }
        }
    }
}