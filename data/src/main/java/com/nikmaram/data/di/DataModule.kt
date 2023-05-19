package com.nikmaram.data.di

import android.content.Context
import androidx.room.Room
import com.nikmaram.data.dao.MusicFileDao
import com.nikmaram.data.dataSource.MusicDataSource
import com.nikmaram.data.dataSource.MusicDataSourceImp
import com.nikmaram.data.db.MusicDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    fun provideMusicDataSource(@ApplicationContext context: Context,musicFileDao: MusicFileDao): MusicDataSource {
        return MusicDataSourceImp(context,musicFileDao)
    }

    @Provides
    @Singleton
    fun provideMusicDatabase(@ApplicationContext context: Context): MusicDatabase {
        return Room.databaseBuilder(
            context,
            MusicDatabase::class.java,
            "music_database"
        ).build()
    }

    @Provides
    fun provideMusicFileDao(database: MusicDatabase): MusicFileDao {
        return database.musicFileDao()
    }
}
