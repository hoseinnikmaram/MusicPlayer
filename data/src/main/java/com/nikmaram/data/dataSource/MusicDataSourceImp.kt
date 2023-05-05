package com.nikmaram.data.dataSource

import android.content.Context
import android.provider.MediaStore
import com.nikmaram.data.model.MusicFile
import com.nikmaram.data.utility.MusicUtil
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MusicDataSourceImp @Inject constructor(private val context:Context) :MusicDataSource {
    override suspend fun getMusicFiles(): List<MusicFile> {
        return MusicUtil.getAllMusicFiles(context)
    }

    override suspend fun getMusicFileById(id: Int): MusicFile? {
        return MusicUtil.getMusicById(id, context)
    }
}