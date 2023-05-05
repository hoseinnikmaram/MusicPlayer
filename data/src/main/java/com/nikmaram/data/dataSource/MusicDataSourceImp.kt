package com.nikmaram.data.dataSource

import android.content.Context
import com.nikmaram.data.model.MusicFile
import com.nikmaram.data.utility.MusicUtil
import com.nikmaram.data.utility.ResultData
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MusicDataSourceImp @Inject constructor(private val context:Context) :MusicDataSource {
    override suspend fun getMusicFiles(): ResultData<MutableList<MusicFile>> {
        return MusicUtil.getAllMusicFiles(context)
    }

    override suspend fun getMusicFileById(id: Long): ResultData<MusicFile> {
        return MusicUtil.getMusicById(id, context)
    }
}