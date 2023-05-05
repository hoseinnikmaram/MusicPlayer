package com.nikmaram.data.model

data class MusicFile(
    val id: Int,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val filePath: String
)
