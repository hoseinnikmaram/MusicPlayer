package com.nikmaram.data.utility

sealed class ResultData<out T : Any> {
    data class Success<out T : Any>(val data: Any?) : ResultData<T>()
    data class Error(val exception: Exception) : ResultData<Nothing>()
}
