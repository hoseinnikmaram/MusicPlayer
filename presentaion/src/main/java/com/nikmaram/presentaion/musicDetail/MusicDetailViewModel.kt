package com.nikmaram.presentaion.musicDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nikmaram.data.model.MusicFile
import com.nikmaram.data.utility.ResultData
import com.nikmaram.domain.useCase.GetMusicFileByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class MusicDetailViewModel @Inject constructor(
    private val getMusicByIdUseCase: GetMusicFileByIdUseCase
) : ViewModel() {

    private val _music = MutableLiveData<MusicFile?>()
    val music: LiveData<MusicFile?> = _music

    fun loadMusicById(musicId: Long) {
        viewModelScope.launch {
            val result = getMusicByIdUseCase(musicId)
            _music.postValue(result)
        }
    }
}
