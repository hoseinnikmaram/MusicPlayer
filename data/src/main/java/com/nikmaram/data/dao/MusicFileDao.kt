package com.nikmaram.data.dao

import androidx.room.*
import com.nikmaram.data.model.MusicFileEntity

@Dao
interface MusicFileDao {
    @Insert
    suspend fun insertMusicFile(musicFileEntity: MusicFileEntity)

    @Insert
    suspend fun insertMusicsFile(musicFileEntity: List<MusicFileEntity>)

    @Update
    suspend fun updateMusicFile(musicFileEntity: MusicFileEntity)

    @Delete
    suspend fun deleteMusicFile(musicFileEntity: MusicFileEntity)

    @Query("SELECT * FROM MusicFileEntity")
    suspend fun getAllMusicFiles(): List<MusicFileEntity>?

    @Query("SELECT COUNT(*) FROM MusicFileEntity")
    suspend fun getMusicFilesCount(): Int

    @Query("SELECT * FROM MusicFileEntity WHERE id = :musicId")
    suspend fun getMusicFileById(musicId: Long): MusicFileEntity?
}
