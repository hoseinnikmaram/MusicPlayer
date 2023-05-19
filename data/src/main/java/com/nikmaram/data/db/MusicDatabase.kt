
package com.nikmaram.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nikmaram.data.dao.MusicFileDao
import com.nikmaram.data.model.MusicFile
import com.nikmaram.data.model.MusicFileEntity

@Database(entities = [MusicFileEntity::class], version = 1)
abstract class MusicDatabase : RoomDatabase() {
    abstract fun musicFileDao(): MusicFileDao
}
