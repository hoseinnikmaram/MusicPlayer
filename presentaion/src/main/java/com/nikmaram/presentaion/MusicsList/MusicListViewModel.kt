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

    init {
        loadMusicList()
    }

    private fun loadMusicList() = viewModelScope.launch {
        _musicListState.value = MusicListState.Loading
        val result = getMusicListUseCase()
        _musicListState.value = when (result) {
            is ResultData.Success -> {
                MusicListState.Loaded(result.data as List<MusicFile>)
            }
            is ResultData.Error -> MusicListState.Error(result.exception.message)
        }
    }

    sealed class MusicListState {
        object Loading : MusicListState()
        data class Loaded(val musicList: List<MusicFile>) : MusicListState()
        data class Error(val message: String?) : MusicListState()
    }
}
