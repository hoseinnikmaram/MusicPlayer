package com.nikmaram.data.model

import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
data class MusicFileEntity(
    @PrimaryKey
    val id: Long,
    val title: String,
    val artist: String,
    val album: String?,
    val duration: Long,
    val filePath: String
    )
