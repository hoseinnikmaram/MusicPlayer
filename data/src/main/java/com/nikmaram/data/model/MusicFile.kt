package com.nikmaram.data.model

import android.graphics.Bitmap
import android.net.Uri

data class MusicFile(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String?,
    val duration: Long,
    val filePath: String,
    val imageUri: Bitmap? = null
)
