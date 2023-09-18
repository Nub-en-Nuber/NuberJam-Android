package com.example.nuberjam.service

import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.os.SystemClock
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.MutableLiveData
import androidx.media.session.MediaButtonReceiver
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.nuberjam.data.model.Music
import com.example.nuberjam.service.MediaNotificationManager.Companion.NOTIFICATION_ID
import com.example.nuberjam.utils.Helper
import com.example.nuberjam.utils.parcelable
import java.io.IOException


class MediaService : Service(), MediaPlayer.OnPreparedListener,
    MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    companion object {
        const val EXTRA_MEDIA_FILE = "extra_media_file"
    }

    private val iBinder: IBinder = LocalBinder()
    private var isForegroundServiceRunning = false
    private var mediaNotificationManager: MediaNotificationManager? = null
    private var mediaSession: MediaSessionCompat? = null
    private var musicImage: Bitmap? = null

    var music: Music? = null
    var mediaPlayer: MediaPlayer? = null
    val isReady: MutableLiveData<Boolean> = MutableLiveData()
    val isPlaying: MutableLiveData<Boolean> = MutableLiveData()

    override fun onBind(intent: Intent): IBinder {
        return iBinder
    }

    internal inner class LocalBinder : Binder() {
        val getService: MediaService = this@MediaService
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        try {
            MediaButtonReceiver.handleIntent(mediaSession, intent)
            if (intent.action == null) {
                music = intent.parcelable(EXTRA_MEDIA_FILE)
                loadImageBitmap()
                initMediaPlayer()
                initMediaSession()
                mediaNotificationManager = MediaNotificationManager(this)
            }
        } catch (e: Exception) {
            stopSelf()
        }
        return START_STICKY
    }

    private fun loadImageBitmap() {
        Glide.with(this)
            .asBitmap()
            .load(music?.albumPhoto)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    musicImage = resource
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
    }

    private fun initMediaPlayer() {
        isReady.postValue(false)
        isPlaying.postValue(false)

        mediaNotificationManager?.notificationManager?.cancelAll()
        stopForeground(STOP_FOREGROUND_REMOVE)
        isForegroundServiceRunning = false

        mediaPlayer = MediaPlayer()
        mediaPlayer?.setOnPreparedListener(this)
        mediaPlayer?.setOnCompletionListener(this)
        mediaPlayer?.setOnErrorListener(this)
        mediaPlayer?.reset()

        val attribute = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()
        mediaPlayer?.setAudioAttributes(attribute)

        try {
            mediaPlayer?.setDataSource(music?.file)
            mediaPlayer?.prepareAsync()
        } catch (e: IOException) {
            e.printStackTrace()
            stopSelf()
        }
    }

    private fun initMediaSession() {
        val mediaButtonReceiver = ComponentName(
            applicationContext,
            MediaButtonReceiver::class.java
        )

        mediaSession = MediaSessionCompat(this, "media_session", mediaButtonReceiver, null).also {
            it.setCallback(object : MediaSessionCompat.Callback() {
                override fun onPlay() {
                    super.onPlay()
                    playOrPauseMedia()
                }

                override fun onPause() {
                    super.onPause()
                    playOrPauseMedia()
                }

                override fun onStop() {
                    super.onStop()
                    stopMedia()
                }

                override fun onSeekTo(pos: Long) {
                    super.onSeekTo(pos)
                    seekMedia(pos.toInt())
                }
            })
            it.isActive = true
        }

        val mediaButtonIntent = Intent(Intent.ACTION_MEDIA_BUTTON)
        mediaButtonIntent.setClass(this, MediaButtonReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this, 0, mediaButtonIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        mediaSession?.setMediaButtonReceiver(pendingIntent)
    }

    private fun getMetadata(): MediaMetadataCompat {
        val builder = MediaMetadataCompat.Builder()
        builder.putString(
            MediaMetadataCompat.METADATA_KEY_TITLE,
            if (music != null) music?.name else "title"
        )
        builder.putString(
            MediaMetadataCompat.METADATA_KEY_ARTIST,
            if (music != null) Helper.concatenateArtist(music?.artist!!) else "artist"
        )
        builder.putString(
            MediaMetadataCompat.METADATA_KEY_ALBUM,
            if (music != null) music?.albumName else "album"
        )
        if (musicImage != null) builder.putBitmap(
            MediaMetadataCompat.METADATA_KEY_ART,
            musicImage
        )
        builder.putLong(
            MediaMetadataCompat.METADATA_KEY_DURATION,
            mediaPlayer?.duration!!.toLong()
        )

        return builder.build()
    }

    private fun setMediaPlaybackState(state: Int) {
        val playbackStateBuilder = PlaybackStateCompat.Builder()
        playbackStateBuilder.setActions(
            PlaybackStateCompat.ACTION_PLAY
                    or PlaybackStateCompat.ACTION_PLAY_PAUSE
                    or PlaybackStateCompat.ACTION_SEEK_TO
                    or PlaybackStateCompat.ACTION_STOP
        )
        playbackStateBuilder.setState(
            state,
            (mediaPlayer?.currentPosition ?: 0).toLong(),
            1.0f,
            SystemClock.elapsedRealtime()
        )
        mediaSession?.setPlaybackState(playbackStateBuilder.build())
    }

    fun playOrPauseMedia() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED)
            mediaNotificationManager?.getNotification(
                mediaSession?.controller?.metadata!!,
                mediaPlayer?.isPlaying!!,
                mediaSession?.sessionToken!!
            )
            isPlaying.postValue(false)
        } else {
            mediaPlayer?.start()
            setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING)
            val notification = mediaNotificationManager?.getNotification(
                mediaSession?.controller?.metadata!!,
                mediaPlayer?.isPlaying!!,
                mediaSession?.sessionToken!!
            )
            if (!isForegroundServiceRunning) {
                startForeground(NOTIFICATION_ID, notification)
                isForegroundServiceRunning = true
            }
            isPlaying.postValue(true)
        }
        mediaSession?.isActive = true
    }

    fun stopMedia() {
        if (mediaPlayer == null) return
        if (mediaPlayer?.isPlaying == true) {
            isReady.postValue(false)
            isPlaying.postValue(false)
            mediaPlayer?.stop()
            mediaNotificationManager?.notificationManager?.cancelAll()
            stopForeground(STOP_FOREGROUND_DETACH)
            isForegroundServiceRunning = false
            stopSelf()
        }
    }

    private fun restartMedia() {
        mediaPlayer?.pause()
        mediaPlayer?.seekTo(0)
        isPlaying.postValue(false)
        setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED)
    }

    fun seekMedia(position: Int) {
        mediaPlayer?.seekTo(position)
        if (mediaPlayer?.isPlaying == true) setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING)
        else setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer != null) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            stopMedia()
            mediaPlayer?.release()
        }
    }

    override fun onPrepared(p0: MediaPlayer?) {
        isReady.postValue(true)
        isPlaying.postValue(false)
        mediaSession?.setMetadata(getMetadata())
        playOrPauseMedia()
    }

    override fun onCompletion(p0: MediaPlayer?) {
        restartMedia()
    }

    override fun onError(p0: MediaPlayer?, p1: Int, p2: Int): Boolean = false
}