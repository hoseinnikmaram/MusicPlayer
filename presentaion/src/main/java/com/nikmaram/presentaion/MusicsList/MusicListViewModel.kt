package com.nikmaram.presentaion.MusicsList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nikmaram.data.model.MusicFile
import com.nikmaram.data.utility.ResultData
import com.nikmaram.domain.useCase.GetAllMusicFileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicListViewModel @Inject constructor(
    private val getMusicListUseCase: GetAllMusicFileUseCase
) : ViewModel() {

    private val _musicListState = MutableLiveData<MusicListState>()
    val musicListState: LiveData<MusicListState> = _musicListState

    fun loadMusicList() = viewModelScope.launch {
        _musicListState.postValue(MusicListState.Loading)
        val result = getMusicListUseCase()
        _musicListState.postValue(
            MusicListState.Loaded(result)
        )
    }

    sealed class MusicListState {
        object Loading : MusicListState()
        data class Loaded(val musicList: List<MusicFile>?) : MusicListState()
    }
}
