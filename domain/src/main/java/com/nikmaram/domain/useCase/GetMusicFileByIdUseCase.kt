package com.nikmaram.domain.useCase

import com.nikmaram.data.dataSource.MusicDataSource
import com.nikmaram.data.model.MusicFile
import javax.inject.Inject

class GetMusicFileByIdUseCase @Inject constructor(private val dataSource: MusicDataSource){
    suspend operator fun invoke(id:Int):MusicFile?{
        return dataSource.getMusicFileById(id)
    }
}