package com.nikmaram.presentaion.MusicsList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.nikmaram.data.model.MusicFile
import com.nikmaram.domain.useCase.GetAllMusicFileAsPagingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicListViewModel @Inject constructor(
    private val getMusicListAsPagingUseCase: GetAllMusicFileAsPagingUseCase
) : ViewModel() {
    private val _musicPagingData = MutableLiveData<PagingData<MusicFile>>()
    val musicPagingData: LiveData<PagingData<MusicFile>> = _musicPagingData
    init {
        getMusicFiles()
    }

    fun getMusicFiles() {
        viewModelScope.launch {
            val result = getMusicListAsPagingUseCase().cachedIn(viewModelScope)
            result.collect { pagingData ->
                _musicPagingData.value = pagingData
            }
        }
    }
}
