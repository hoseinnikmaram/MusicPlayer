package com.nikmaram.presentaion.utility

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.navigation.NavDeepLinkBuilder
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.nikmaram.presentaion.R

class DescriptionAdapter(
    private val context: Context,
) : PlayerNotificationManager.MediaDescriptionAdapter {

    override fun getCurrentContentTitle(player: Player): CharSequence {
        return player.mediaMetadata.title ?: "Unknown" as CharSequence
    }

    override fun createCurrentContentIntent(player: Player): PendingIntent? {
        return NavDeepLinkBuilder(context)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.musicDetailFragment)
            .createPendingIntent()
    }

    override fun getCurrentContentText(player: Player): CharSequence {
        return player.mediaMetadata.artist ?: "Unknown" as CharSequence
    }

    override fun getCurrentLargeIcon(
        player: Player,
        callback: PlayerNotificationManager.BitmapCallback
    ): Bitmap? {
        val bytes = player.mediaMetadata.artworkData
        return if (bytes != null) {
            callback.onBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.size))
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } else {
            null
        }
    }
}