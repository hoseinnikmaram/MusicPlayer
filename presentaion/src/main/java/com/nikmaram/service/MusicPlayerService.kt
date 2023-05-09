package com.nikmaram.service
import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.nikmaram.data.model.MusicFile
import com.nikmaram.presentaion.MEDIA_SESSION_TAG
import com.nikmaram.presentaion.NOTIFICATION_ID
import com.nikmaram.presentaion.model.PlaybackAction
import com.nikmaram.presentaion.utility.NotificationUtils
import com.nikmaram.presentaion.utility.NotificationUtils.createNotification
import com.nikmaram.presentaion.utility.parcelableArrayList

class MusicPlayerService : Service(), Player.Listener {
    private lateinit var player: ExoPlayer
    private lateinit var dataSourceFactory: DataSource.Factory
    private lateinit var notificationManager: NotificationManagerCompat
    private lateinit var mediaSessionCompat: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector

    private var musicFiles: List<MusicFile> = emptyList()
    private var currentIndex = -1
    private var isPlaying = false

    override fun onCreate() {
        super.onCreate()
        player = ExoPlayer.Builder(this).build()
        player.addListener(this)
        dataSourceFactory = DefaultDataSourceFactory(this, Util.getUserAgent(this, "music-player"))
        notificationManager = NotificationManagerCompat.from(this)
        mediaSessionCompat = MediaSessionCompat(this, MEDIA_SESSION_TAG)
        mediaSessionConnector.setPlayer(player)
        mediaSessionCompat.isActive = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            PlaybackAction.SEEK.name -> playSeekTo(intent)
            PlaybackAction.PLAY_PAUSE.name -> togglePlayback()
            PlaybackAction.PREV.name -> playPrevious()
            PlaybackAction.NEXT.name -> playNext()
            else -> {
                musicFiles = intent?.parcelableArrayList("musicFiles") ?: emptyList()
                val index = intent?.extras?.getInt("currentIndex") ?: -1
                if (musicFiles.isNotEmpty() && index >= 0 && index < musicFiles.size) {
                    currentIndex = index
                    playCurrent()
                } else {
                    stopSelf()
                }
            }
        }
        return START_NOT_STICKY
    }

    private fun playSeekTo(intent: Intent) {
        val position = intent.getIntExtra("position", 0)
        player.seekTo(position.toLong())
    }

    private fun play() {
        if (musicFiles.isNotEmpty() && currentIndex >= 0 && currentIndex < musicFiles.size) {
            val musicFile = musicFiles[currentIndex]
            val mediaItem = MediaItem.fromUri(Uri.parse(musicFile.filePath))
            // Set the media item to the player
            player.setMediaItem(mediaItem)
            // Prepare the player
            player.prepare()
            player.play()
            player.playWhenReady = true
            isPlaying = true
            NotificationUtils.createChannel(this)
            // Create and show the notification
            val notification = createNotification(
                this,
                musicFile,
                PlaybackStateCompat.STATE_PLAYING,
                0,
                player.duration.toInt()
            )
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun pause() {
        player.playWhenReady = false
        isPlaying = false
        // Update the notification to show that the playback has paused
        updateNotification(PlaybackStateCompat.STATE_PAUSED, player.currentPosition.toInt())
    }

    private fun playPrevious() {
        if (musicFiles.isNotEmpty()) {
            currentIndex = (currentIndex - 1 + musicFiles.size) % musicFiles.size
            playCurrent()
        }
    }

    private fun playNext() {
        if (musicFiles.isNotEmpty()) {
            currentIndex = (currentIndex + 1) % musicFiles.size
            playCurrent()
        }
    }

    private fun playCurrent() {
        pause()
        play()
    }

    private fun updateNotification(playbackState: Int, progress: Int) {
        if (musicFiles.isNotEmpty() && currentIndex >= 0 && currentIndex < musicFiles.size) {
            val musicFile = musicFiles[currentIndex]
            val notification = createNotification(this, musicFile, playbackState, progress, player.duration.toInt())
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) return
            notificationManager.notify(NOTIFICATION_ID, notification)
        }
    }

    override fun onPlaybackStateChanged(state: Int) {
        if (state == Player.STATE_ENDED) {
            playNext()
        } else {
            updateNotification(state, player.currentPosition.toInt())
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        this.isPlaying = isPlaying
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    private fun togglePlayback() {
        isPlaying = if (isPlaying) {
            player.pause()
            false
        } else {
            player.play()
            true
        }
        updateNotification(
            if (isPlaying) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED,
            player.currentPosition.toInt()
        )
    }


}


