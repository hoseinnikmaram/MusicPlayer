package com.nikmaram.data.dataSource

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.nikmaram.data.dao.MusicFileDao
import com.nikmaram.data.mappers.toMusicFile
import com.nikmaram.data.mappers.toMusicFileEntity
import com.nikmaram.data.model.MusicFile
import com.nikmaram.data.utility.MusicUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking


class MusicDataSourceImp (private val context:Context,private val musicFileDao: MusicFileDao) :MusicDataSource {
    override suspend fun getMusicFilesAsPaging(): Flow<PagingData<MusicFile>> {
        if (musicFileDao.getMusicFilesCount() == 0) {
            MusicUtil.getAllMusicFiles(context)?.let { musics ->
                musicFileDao.insertMusicsFile(
                    musics.map { it.toMusicFileEntity() }
                )
            }
        }
        return Pager(
            config = PagingConfig(15, enablePlaceholders = false)
        ) {
            musicFileDao.getAllMusicFilesAsPaging()
        }.flow.map { list -> list.map { it.toMusicFile() } }
    }


    override suspend fun getMusicFileById(id: Long): MusicFile? {
        return musicFileDao.getMusicFileById(id)?.toMusicFile()
    }
}