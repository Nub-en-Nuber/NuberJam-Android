package com.example.nuberjam.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.media.session.MediaButtonReceiver
import com.example.nuberjam.R
import com.example.nuberjam.ui.main.MainActivity
import com.example.nuberjam.utils.Helper

class MediaNotificationManager(private val mediaService: MediaService) {
    companion object {
        const val NOTIFICATION_ID = 100

        const val CHANNEL_ID = "notification_ch_01"
        const val CHANNEL_NAME = "notification_ch_01"
        const val CHANNEL_DESC = "notification_ch_01"
        const val REQUEST_CODE = 1
    }

    var notificationManager: NotificationManager =
        mediaService.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager

    private var mPlayAction: NotificationCompat.Action = NotificationCompat.Action(
        R.drawable.ic_play_blue,
        "play",
        MediaButtonReceiver.buildMediaButtonPendingIntent(
            mediaService, PlaybackStateCompat.ACTION_PLAY
        )
    )

    private var mPauseAction: NotificationCompat.Action = NotificationCompat.Action(
        R.drawable.ic_pause_blue,
        "pause",
        MediaButtonReceiver.buildMediaButtonPendingIntent(
            mediaService, PlaybackStateCompat.ACTION_PAUSE
        )
    )

    init {
        notificationManager.cancelAll()
    }

    fun getNotification(
        metadata: MediaMetadataCompat, isPlaying: Boolean, token: MediaSessionCompat.Token
    ): Notification {
        val description = metadata.description
        val builder: NotificationCompat.Builder =
            buildNotification(token, isPlaying, description)
        val notification = builder.build()
        notification.flags = Notification.FLAG_NO_CLEAR or Notification.FLAG_ONGOING_EVENT
        notificationManager.notify(NOTIFICATION_ID, notification)
        return notification
    }

    private fun buildNotification(
        token: MediaSessionCompat.Token,
        isPlaying: Boolean,
        description: MediaDescriptionCompat
    ): NotificationCompat.Builder {
        val builder = NotificationCompat.Builder(mediaService, CHANNEL_ID)
        builder
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(token)
                    .setShowCancelButton(true)
                    .setCancelButtonIntent(
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                            mediaService, PlaybackStateCompat.ACTION_STOP
                        )
                    )
            )
            .setContentTitle(description.title)
            .setContentText(description.subtitle)
            .setSubText(description.description)
            .setSmallIcon(R.drawable.logo_nuberjam_notitle_transparent)
            .setContentIntent(createContentIntent())
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setSound(null)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(false)
            .setColor(ContextCompat.getColor(mediaService, R.color.primary_main))
            .setDeleteIntent(
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    mediaService, PlaybackStateCompat.ACTION_STOP
                )
            )

        if (Helper.isAndroidOOrHigher()) {
            createNotificationChannel()
            builder.setChannelId(CHANNEL_ID)
        }
        builder.addAction(if (isPlaying) mPauseAction else mPlayAction)
        return builder
    }

    private fun createNotificationChannel() {
        val mChannel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        mChannel.description = CHANNEL_DESC
        notificationManager.createNotificationChannel(mChannel)
    }

    private fun createContentIntent(): PendingIntent? {
        val intentNotification = Intent(mediaService, MainActivity::class.java)
        intentNotification.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingFlags: Int =
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        return PendingIntent.getActivity(
            mediaService,
            REQUEST_CODE,
            intentNotification,
            pendingFlags
        )
    }
}