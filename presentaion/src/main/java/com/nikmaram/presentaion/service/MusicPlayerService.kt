package com.nikmaram.presentaion.service
import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.CountDownTimer
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import com.google.android.exoplayer2.*
import com.nikmaram.data.model.MusicFile
import com.nikmaram.presentaion.MEDIA_SESSION_TAG
import com.nikmaram.presentaion.NOTIFICATION_ID
import com.nikmaram.presentaion.model.ServiceContentWrapper
import com.nikmaram.presentaion.utility.NotificationUtils
import com.nikmaram.presentaion.utility.NotificationUtils.createNotification
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicPlayerService @Inject constructor() : LifecycleService() {
    private lateinit var notificationManager: NotificationManagerCompat
    private lateinit var mediaSessionCompat: MediaSessionCompat
    private var musicFiles: List<MusicFile> = emptyList()
    private val lifecycleRegistry = LifecycleRegistry(this)
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
        fun togglePlayback(): Boolean {
            isPlayingLiveData.value = if (isPlayingLiveData.value == true) {
                onPause()
                false
            } else {
                onPlay()
                true
            }
            return isPlayingLiveData.value!!
        }

        fun onNext() { if (exoPlayer.hasNextMediaItem()) exoPlayer.seekToNextMediaItem() }
        fun onPrevious() { if (exoPlayer.hasPreviousMediaItem()) exoPlayer.seekToPreviousMediaItem() }

        fun onSeekTo(pos: Long) {
            exoPlayer.seekTo(pos)
            trackDurationLiveData.value = pos
        }

        private var timer: CountDownTimer? = null
        private const val TIMER_INTERVAL: Long = 250
    }
    override fun onCreate() {
        super.onCreate()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        mediaSessionCompat = MediaSessionCompat(this, MEDIA_SESSION_TAG)
        mediaSessionCompat.isActive = true
        exoPlayer = ExoPlayer.Builder(this).build()
        exoPlayer.addListener(getPlayerListener())
        isPlayingLiveData.value = false
        NotificationUtils.createChannel(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        isRunningService.value = true

        serviceContentLiveData.observe(this) {
            exoPlayer.clearMediaItems()
            if (!it.playlist.isNullOrEmpty())
            for (musicFile in it.playlist!!)
                exoPlayer.addMediaItem(MediaItem.fromUri(musicFile.filePath))
            exoPlayer.seekToDefaultPosition(it.position)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
            isRunningService.value = true
            val notification = getCurrentMusicFile()?.let { music ->
                createNotification(
                    this,
                    music,
                    PlaybackStateCompat.STATE_PLAYING,
                    0,
                    exoPlayer.duration.toInt()
                )
            }
            startForeground(NOTIFICATION_ID, notification)
        }
        return START_STICKY
    }

    public fun updateNotification(playbackState: Int, progress: Int) {
            val musicFile = getCurrentMusicFile() ?: return
        val notification =
                    createNotification(this,
                        musicFile,
                        playbackState,
                        progress,
                        exoPlayer.duration.toInt())
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) return

            notificationManager.notify(NOTIFICATION_ID, notification)

    }
    override fun onDestroy() {
        super.onDestroy()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        exoPlayer.release()
        isRunningService.value = false
    }

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
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

    private fun getPlayerListener() : Player.Listener = object : Player.Listener {

        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
            mediaMetadataLiveData.value = mediaMetadata
            contentPositionLiveData.value = exoPlayer.currentMediaItemIndex
            trackDurationLiveData.value = 0
            Log.d("DEBUG", "IS PLAYABLE: ${mediaMetadata}\tIndex: ${exoPlayer.currentMediaItemIndex}")
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            isPlayingLiveData.value = isPlaying

            if (isPlaying) {
                startTimer(exoPlayer.duration)
            } else {
                stopTimer()
            }
        }

        override fun onPlaybackStateChanged(state: Int) {
            if (state == Player.STATE_ENDED) {
                onNext()
            } else {
                updateNotification(state, trackDurationLiveData.value?.toInt() ?: 0)
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            Toast.makeText(this@MusicPlayerService, "Error: ${error.errorCodeName}", Toast.LENGTH_SHORT).show()
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


