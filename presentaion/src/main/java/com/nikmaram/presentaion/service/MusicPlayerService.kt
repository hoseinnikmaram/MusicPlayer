package com.nikmaram.presentaion.service

import android.Manifest
import android.app.Notification
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.CountDownTimer
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.nikmaram.data.model.MusicFile
import com.nikmaram.presentaion.CHANNEL_ID
import com.nikmaram.presentaion.MEDIA_SESSION_TAG
import com.nikmaram.presentaion.NOTIFICATION_ID
import com.nikmaram.presentaion.model.PlaybackAction
import com.nikmaram.presentaion.model.ServiceContentWrapper
import com.nikmaram.presentaion.utility.DescriptionAdapter
import com.nikmaram.presentaion.utility.NotificationUtils
import com.nikmaram.presentaion.utility.NotificationUtils.createNotification


class MusicPlayerService : LifecycleService() {
    private lateinit var notificationManager: NotificationManagerCompat
    private lateinit var mediaSessionCompat: MediaSessionCompat
    private lateinit var playerNotificationManager: PlayerNotificationManager

    companion object {

        private var isRunningService = MutableLiveData(false)
        private lateinit var exoPlayer: ExoPlayer

        private val serviceContentLiveData = MutableLiveData<ServiceContentWrapper>()
        private val mediaMetadataLiveData = MutableLiveData<MediaMetadata>()
        private val isPlayingLiveData = MutableLiveData<Boolean>()
        private val trackDurationLiveData = MutableLiveData<Long>(0)
        private val contentPositionLiveData = MutableLiveData<Int>()

        fun startService(context: Context, content: ServiceContentWrapper) {

            updateContent(content)

            if (isRunningService.value != true) {
                val service = Intent(context, MusicPlayerService::class.java)
                ContextCompat.startForegroundService(context, service)
            }
        }

        private fun updateContent(content: ServiceContentWrapper) {
            serviceContentLiveData.value = content
        }

        fun getServiceContent(): ServiceContentWrapper {
            return ServiceContentWrapper(
                position = exoPlayer.currentMediaItemIndex,
                playlist = serviceContentLiveData.value?.playlist,
                playerPosition = exoPlayer.contentPosition
            )
        }

        fun isRunningService(): LiveData<Boolean> = isRunningService

        fun isPlaying(): LiveData<Boolean> = isPlayingLiveData

        fun getCurrentMetadata(): LiveData<MediaMetadata> = mediaMetadataLiveData

        fun getDuration(): Long = exoPlayer.duration

        fun getCurrentDuration(): LiveData<Long> = trackDurationLiveData

        fun getContentPosition(): LiveData<Int> = contentPositionLiveData

        fun getCurrentMusicFile(): MusicFile? {
            val pos = contentPositionLiveData.value
            return if (pos != null)
                serviceContentLiveData.value?.playlist?.get(pos)
            else null
        }

        fun onPlay() {
            exoPlayer.play()
        }

        fun onPause() {
            exoPlayer.pause()
        }

        fun togglePlayback() {
            if (isPlayingLiveData.value == true)
                onPause()
            else
                onPlay()
        }

        fun onNext() {
            if (exoPlayer.hasNextMediaItem()) exoPlayer.seekToNextMediaItem()
        }

        fun onPrevious() {
            if (exoPlayer.hasPreviousMediaItem()) exoPlayer.seekToPreviousMediaItem()
        }

        fun onSeekTo(pos: Long) {
            exoPlayer.seekTo(pos)
            trackDurationLiveData.value = pos
        }

        private var timer: CountDownTimer? = null
        private const val TIMER_INTERVAL: Long = 250
    }

    override fun onCreate() {
        super.onCreate()
        mediaSessionCompat = MediaSessionCompat(this, MEDIA_SESSION_TAG)
        mediaSessionCompat.isActive = true
        exoPlayer = ExoPlayer.Builder(this).build()
        exoPlayer.addListener(getPlayerListener())
        isPlayingLiveData.value = false
        NotificationUtils.createChannel(this)
        notificationManager = NotificationManagerCompat.from(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        when (intent?.action) {
            PlaybackAction.PLAY_PAUSE.name -> togglePlayback()
            PlaybackAction.PREV.name -> onPrevious()
            PlaybackAction.NEXT.name -> onNext()
        }
        isRunningService.value = true

        serviceContentLiveData.observe(this) {
            setupPlayerNotificationManager()
            exoPlayer.clearMediaItems()
            if (!it.playlist.isNullOrEmpty())
                for (musicFile in it.playlist!!)
                    exoPlayer.addMediaItem(MediaItem.fromUri(musicFile.filePath))
            exoPlayer.seekToDefaultPosition(it.position)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
            isRunningService.value = true
        }
        return START_STICKY
    }



    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
        isRunningService.value = false
    }


    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        isRunningService.value = false
        stopSelf()
    }

    private fun getPlayerListener(): Player.Listener = object : Player.Listener {

        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
            mediaMetadataLiveData.value = mediaMetadata
            contentPositionLiveData.value = exoPlayer.currentMediaItemIndex
            trackDurationLiveData.value = 0
            Log.d(
                "DEBUG",
                "IS PLAYABLE: ${mediaMetadata}\tIndex: ${exoPlayer.currentMediaItemIndex}"
            )
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            isPlayingLiveData.value = isPlaying

            if (isPlaying) {
                startTimer(exoPlayer.duration)
            } else {
                stopTimer()
            }
        }


        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            Toast.makeText(
                this@MusicPlayerService,
                "Error: ${error.errorCodeName}",
                Toast.LENGTH_SHORT
            ).show()
            stopSelf()
        }

    }
    private fun setupPlayerNotificationManager() {
        playerNotificationManager = PlayerNotificationManager
            .Builder(this, NOTIFICATION_ID, CHANNEL_ID)
            .setMediaDescriptionAdapter(DescriptionAdapter(context = this))
            .setNotificationListener(getNotificationListener()).build()
        playerNotificationManager.setPlayer(exoPlayer)
    }
    private fun getNotificationListener(): PlayerNotificationManager.NotificationListener =
        object : PlayerNotificationManager.NotificationListener {

            override fun onNotificationPosted(
                notificationId: Int,
                notification: Notification,
                ongoing: Boolean
            ) {
                startForeground(notificationId, notification)
            }

            override fun onNotificationCancelled(
                notificationId: Int,
                dismissedByUser: Boolean
            ) {
                stopSelf()
            }
        }


    private fun startTimer(duration: Long) {

        val startFrom = trackDurationLiveData.value ?: 0
        var timerDuration = duration

        if (startFrom != 0.toLong()) {
            timerDuration = duration - startFrom
        }

        timer = object : CountDownTimer(timerDuration, TIMER_INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {
                trackDurationLiveData.value = trackDurationLiveData.value?.plus(TIMER_INTERVAL)
            }

            override fun onFinish() {
                trackDurationLiveData.value = trackDurationLiveData.value?.plus(TIMER_INTERVAL)
            }
        }.start()
    }

    private fun stopTimer() {
        timer?.cancel()
    }


}


