package com.nikmaram.data.dataSource

import com.nikmaram.data.model.MusicFile
import com.nikmaram.data.utility.ResultData

interface MusicDataSource {
    suspend fun getMusicFiles(): List<MusicFile>?
    suspend fun getMusicFileById(id: Long): MusicFile?
}