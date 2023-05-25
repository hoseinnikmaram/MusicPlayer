package com.nikmaram.domain.useCase

import androidx.paging.PagingData
import com.nikmaram.data.dataSource.MusicDataSource
import com.nikmaram.data.model.MusicFile
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllMusicFileAsPagingUseCase @Inject constructor(private val dataSource: MusicDataSource){
     suspend operator fun invoke() : Flow<PagingData<MusicFile>> {
        return dataSource.getMusicFilesAsPaging()
    }
}