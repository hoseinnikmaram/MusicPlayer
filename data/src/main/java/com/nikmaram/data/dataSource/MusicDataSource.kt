package com.nikmaram.data.dataSource

import androidx.paging.PagingData
import com.nikmaram.data.model.MusicFile
import kotlinx.coroutines.flow.Flow

interface MusicDataSource {
    suspend fun getMusicFilesAsPaging(): Flow<PagingData<MusicFile>>
    suspend fun getMusicFileById(id: Long): MusicFile?
}