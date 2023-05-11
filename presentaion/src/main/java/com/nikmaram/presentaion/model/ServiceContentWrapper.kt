package com.nikmaram.presentaion.model

import android.os.Parcelable
import com.nikmaram.data.model.MusicFile
import kotlinx.parcelize.Parcelize

@Parcelize
data class ServiceContentWrapper(
    var playlist: MutableList<MusicFile>? = null,
    var playerPosition: Long = 0,
    var position: Int = 0,
) : Parcelable