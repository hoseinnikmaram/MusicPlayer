package com.nikmaram.presentaion.utility

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.nikmaram.data.model.MusicFile
import com.nikmaram.presentaion.R
import com.nikmaram.presentaion.model.PlaybackAction
import com.nikmaram.presentaion.service.MusicPlayerService

object NotificationUtils {
     fun createNotification(
         mediaSessionCompat: MediaSessionCompat,
         context: Context,
         musicFile: MusicFile,
         playbackState: Int,
         progress: Int,
         duration: Int
     ): Notification {
        // Create a pending intent that will launch the music player when the notification is tapped
        val pendingIntent = NavDeepLinkBuilder(context)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.musicDetailFragment)
            //.setArguments(bundleOf("musicId" to musicFile.id))
            .createPendingIntent()

        // Create a notification with a custom layout
        val notificationLayout =
            RemoteViews(context.packageName, R.layout.custom_notification).apply {
                setTextViewText(R.id.tvTitle, musicFile.title)
                setTextViewText(R.id.tvArtist, musicFile.artist)

                // Set the artwork image, or a default image if the artwork is not available
                val bitmap = musicFile.imageUri
                setImageViewBitmap(
                    R.id.ivAlbumArt,
                    bitmap ?: BitmapFactory.decodeResource(context.resources, R.drawable.ic_round_audiotrack_24)
                )

                // Set the playback controls' click listeners
                setOnClickPendingIntent(
                    R.id.btnPrev,
                    createPlaybackIntent(context, PlaybackAction.PREV)
                )
                setOnClickPendingIntent(
                    R.id.btnPlayPause,
                    createPlaybackIntent(context, PlaybackAction.PLAY_PAUSE)
                )
                setOnClickPendingIntent(
                    R.id.btnNext,
                    createPlaybackIntent(context, PlaybackAction.NEXT)
                )
                setOnClickPendingIntent(
                    R.id.progress_horizontal,
                    createPlaybackIntent(context, PlaybackAction.SEEK,))

                // Set the progress bar's max value and current progress
                setProgressBar(R.id.progress_horizontal, 100, progress, false)

            }

        // Set up the notification's playback controls
        val playPauseIcon =
            if (playbackState == PlaybackStateCompat.STATE_PLAYING) R.drawable.ic_round_pause_24_small else R.drawable.ic_round_play_arrow_24_small
        return NotificationCompat.Builder(context, "music_player_channel")
            .setContentTitle(musicFile.title)
            .setContentText(musicFile.artist)
            .setSmallIcon(R.drawable.ic_round_audiotrack_24)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(pendingIntent)
            .setCustomContentView(notificationLayout)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSessionCompat.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2)
            )
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .setAutoCancel(false)
            .setProgress(duration, progress, false)
            .addAction(
                NotificationCompat.Action(
                    playPauseIcon,
                    if (playbackState == PlaybackStateCompat.STATE_PLAYING) "Pause" else "Play",
                    createPlaybackIntent(context, PlaybackAction.PLAY_PAUSE)
                )
            )
            .build()
    }


    fun createChannel(context: Context){
        // Create a notification channel for Android Oreo and higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "music_player_channel"
            val channel = NotificationChannel(
                channelId,
                "Music Player Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Music Player Notification Channel"
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createPlaybackIntent(context: Context, action: PlaybackAction, position: Int = 0): PendingIntent {
        val intent = Intent(context, MusicPlayerService::class.java).apply {
            this.action = action.name
            putExtra("position", position)
        }
        return PendingIntent.getService(context, action.ordinal, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}

