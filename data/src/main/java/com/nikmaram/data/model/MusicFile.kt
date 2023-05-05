package com.nikmaram.data.model

data class MusicFile(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val filePath: String
)
