package com.nikmaram.musicplayer

import android.app.Application
import android.provider.MediaStore
import com.nikmaram.presentaion.utility.MusicContentObserver
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application() {
    @Inject
    lateinit var musicContentObserver: MusicContentObserver
    override fun onCreate() {
        super.onCreate()
        registerMusicContentObserver()
    }

    private fun registerMusicContentObserver() {
        val contentResolver = applicationContext.contentResolver
        val observerUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        contentResolver.registerContentObserver(
            observerUri,
            true,
            musicContentObserver
        )
    }
}