package com.nikmaram.data.dataSource

import com.nikmaram.data.model.MusicFile
import com.nikmaram.data.utility.ResultData

interface MusicDataSource {
    suspend fun getMusicFiles(): ResultData<List<MusicFile>>
    suspend fun getMusicFileById(id: Int): ResultData<MusicFile>
}