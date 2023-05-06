package com.nikmaram.data.di

import android.content.Context
import com.nikmaram.data.dataSource.MusicDataSource
import com.nikmaram.data.dataSource.MusicDataSourceImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    fun provideMusicDataSource(@ApplicationContext context: Context): MusicDataSource {
        return MusicDataSourceImp(context)
    }
}
