package com.nikmaram.presentaion.utility

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.nfc.Tag
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nikmaram.data.dao.MusicFileDao
import com.nikmaram.data.mappers.toMusicFileEntityList
import com.nikmaram.data.mappers.toMusicFileList
import com.nikmaram.data.model.MusicFile
import com.nikmaram.data.utility.MusicUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicContentObserver @Inject constructor(
    @ApplicationContext private val context: Context,
    private val musicFileDao: MusicFileDao,
    ): ContentObserver(Handler(Looper.getMainLooper())) {
    private val TAG: String = MusicContentObserver::class.simpleName ?: ""
    private val _addedMusicFilesLiveData = MutableLiveData<List<MusicFile>>()
val addedMusicFilesLiveData:LiveData<List<MusicFile>> = _addedMusicFilesLiveData
private val _deletedMusicFilesLiveData = MutableLiveData<List<Long>>()
val deletedMusicFilesLiveData: LiveData<List<Long>> = _deletedMusicFilesLiveData
    override fun onChange(selfChange: Boolean, uri: Uri?) {
        super.onChange(selfChange, uri)
        checkUpdateMusicFiles()
    }

    fun checkUpdateMusicFiles() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
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
                val addedMusicFilesEntity =
                    updatedMusicFiles.filter { addedMusicIds.contains(it.id) }
                        .toMusicFileEntityList()
                val deletedMusicFilesEntity =
                    databaseMusicFiles.filter { deletedMusicIds.contains(it.id) }
                        .toMusicFileEntityList()
                musicFileDao.insertMusicsFile(addedMusicFilesEntity)
                musicFileDao.deleteMusicFiles(deletedMusicFilesEntity)
                _addedMusicFilesLiveData.postValue(addedMusicFilesEntity.toMusicFileList())
                _deletedMusicFilesLiveData.postValue(deletedMusicIds)
            } catch (ex: Exception) {
                Log.e(TAG, ex.message.toString())
            }
        }
    }
}