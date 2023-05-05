package com.nikmaram.data.utility

import android.content.Context
import android.provider.MediaStore
import com.nikmaram.data.model.MusicFile

object MusicUtil {
    private val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.DATA
    )
    private const val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
    private const val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"
    private const val selectionId =
        "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media._ID} = ?"

    fun getAllMusicFiles(context: Context): ResultData<MutableList<MusicFile>> {
        val musicFiles = mutableListOf<MusicFile>()
        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            sortOrder
        )
        try {
            cursor?.apply {
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

                while (cursor.moveToNext()) {
                    val id = cursor.getInt(idColumn)
                    val title = cursor.getString(titleColumn)
                    val artist = cursor.getString(artistColumn)
                    val duration = cursor.getLong(durationColumn)
                    val album = cursor.getString(albumColumn)
                    val data = cursor.getString(dataColumn)
                    musicFiles.add(MusicFile(id, title, artist, album, duration, data))
                }
            }
            return ResultData.Success(musicFiles)
        } catch (ex: Exception) {
            return ResultData.Error(ex)
        }
        finally {
            cursor?.close()
        }
    }

    fun getMusicById(id: Int, context: Context): ResultData<MusicFile> {
        val selectionArgs = arrayOf(id.toString())
        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selectionId,
            selectionArgs,
            null
        )
        try {
            cursor?.apply {
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                if (cursor.moveToFirst()) {
                    val id = cursor.getInt(idColumn)
                    val title = cursor.getString(titleColumn)
                    val artist = cursor.getString(artistColumn)
                    val duration = cursor.getLong(durationColumn)
                    val album = cursor.getString(albumColumn)
                    val data = cursor.getString(dataColumn)
                    return ResultData.Success(MusicFile(id, title, artist, album, duration, data))
                }
            }
        }
        catch (ex: Exception) {
            return ResultData.Error(ex)
        }
        finally {
            cursor?.close()
        }
        return ResultData.Success(null)
    }
}

