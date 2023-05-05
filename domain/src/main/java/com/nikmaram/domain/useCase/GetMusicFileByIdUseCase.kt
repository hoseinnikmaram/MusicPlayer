package com.nikmaram.domain.useCase

import com.nikmaram.data.dataSource.MusicDataSource
import com.nikmaram.data.model.MusicFile
import com.nikmaram.data.utility.ResultData
import javax.inject.Inject

class GetMusicFileByIdUseCase @Inject constructor(private val dataSource: MusicDataSource){
    suspend operator fun invoke(id:Long): ResultData<MusicFile> {
        return dataSource.getMusicFileById(id)
    }
}