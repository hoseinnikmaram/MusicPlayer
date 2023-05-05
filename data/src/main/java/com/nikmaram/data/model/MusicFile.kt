package com.nikmaram.data.model

import android.net.Uri

data class MusicFile(
    val id: Int,
    val title: String,
    val artist: String,
    val album: String?,
    val duration: Long,
    val filePath: String,
    val imageUri: Uri? = null
)
