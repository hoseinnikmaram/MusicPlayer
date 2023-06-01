package com.nikmaram.presentaion.utility

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import com.nikmaram.data.dao.MusicFileDao
import com.nikmaram.data.mappers.toMusicFileEntityList
import com.nikmaram.data.mappers.toMusicFileList
import com.nikmaram.data.utility.MusicUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class MusicContentObserver @Inject constructor(
    @ApplicationContext private val context: Context,
    private val musicFileDao: MusicFileDao,
    ): ContentObserver(Handler(Looper.getMainLooper())) {

    override fun onChange(selfChange: Boolean, uri: Uri?) {
        super.onChange(selfChange, uri)
        CoroutineScope(Dispatchers.IO).launch {
            val updatedMusicFiles = MusicUtil.getAllMusicFiles(context)
            val databaseMusicFiles = musicFileDao.getAllMusicFiles().toMusicFileList()
            if (updatedMusicFiles == null) return@launch
            val addedMusicIds = updatedMusicFiles
                .map { it.id }
                .filterNot { updatedMusicId ->
                    databaseMusicFiles.map { it.id }
                        .contains(updatedMusicId)
                }
            val deletedMusicIds = databaseMusicFiles
                .map { it.id }
                .filterNot { databaseMusicId ->
                    updatedMusicFiles.map { it.id }
                        .contains(databaseMusicId)
                }
            val addedMusicFilesEntity = updatedMusicFiles.filter { addedMusicIds.contains(it.id) }.toMusicFileEntityList()
            val deletedMusicFilesEntity = databaseMusicFiles.filter { deletedMusicIds.contains(it.id) }.toMusicFileEntityList()
            musicFileDao.insertMusicsFile(addedMusicFilesEntity)
            musicFileDao.deleteMusicFiles(deletedMusicFilesEntity)
        }
    }
}