package com.nikmaram.data.dataSource

import com.nikmaram.data.model.MusicFile

interface MusicDataSource {
    suspend fun getMusicFiles(): List<MusicFile>
    suspend fun getMusicFileById(id: Int): MusicFile?
}