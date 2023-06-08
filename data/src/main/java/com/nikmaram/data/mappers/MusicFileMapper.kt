package com.nikmaram.data.mappers

import com.nikmaram.data.model.MusicFile
import com.nikmaram.data.model.MusicFileEntity


fun MusicFileEntity.toMusicFile():MusicFile {
    return MusicFile(
        id,
        title,
        artist,
        album,
        duration,
        filePath
    )
}

fun MusicFile.toMusicFileEntity():MusicFileEntity {
    return MusicFileEntity(
        id,
        title,
        artist,
        album,
        duration,
        filePath
    )
}

fun List<MusicFileEntity>.toMusicFileList(): List<MusicFile> {
    val returnableList = mutableListOf<MusicFile>()
    for (n in this.indices) {
        returnableList.add(this[n].toMusicFile())
    }
    return returnableList
}

fun List<MusicFile>.toMusicFileEntityList() : List<MusicFileEntity> {
    val returnableList = mutableListOf<MusicFileEntity>()
    for (n in this.indices){
        returnableList.add(this[n].toMusicFileEntity())
    }
    return returnableList
}
