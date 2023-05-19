package com.nikmaram.data.dataSource

import android.content.Context
import com.nikmaram.data.dao.MusicFileDao
import com.nikmaram.data.mappers.toMusicFile
import com.nikmaram.data.mappers.toMusicFileEntity
import com.nikmaram.data.model.MusicFile
import com.nikmaram.data.utility.MusicUtil
import com.nikmaram.data.utility.ResultData
import javax.inject.Inject
import javax.inject.Singleton



class MusicDataSourceImp (private val context:Context,private val musicFileDao: MusicFileDao) :MusicDataSource {
    override suspend fun getMusicFiles(): List<MusicFile>? {
        if (musicFileDao.getMusicFilesCount() == 0){
            MusicUtil.getAllMusicFiles(context)?.let { musics ->
                musicFileDao.insertMusicsFile(
                    musics.map { it.toMusicFileEntity() }
                )
            }
        }
        return musicFileDao.getAllMusicFiles()?.map { it.toMusicFile() }
    }

    override suspend fun getMusicFileById(id: Long): MusicFile? {
        return musicFileDao.getMusicFileById(id)?.toMusicFile()
    }
}