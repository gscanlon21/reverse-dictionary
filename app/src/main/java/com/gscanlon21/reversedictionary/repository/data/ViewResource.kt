package com.gscanlon21.reversedictionary.repository.data

import android.util.Log

sealed class ViewResource<out T> {
    sealed class WithData<out T>(open val data: T?) : ViewResource<T>() {
        data class Success<out T>(override val data: T) : WithData<T>(data)
        data class Loading<out T>(override val data: T?) : WithData<T>(data)
    }

    data class Error(val exception: Throwable?) : ViewResource<Nothing>() {
        init {
            if (exception != null) {
                Log.w(Error::class.simpleName, exception)
            } else {
                Log.w(Error::class.simpleName, "ViewResource Error")
            }
        }
    }

    fun hasData(): Boolean {
        return when (this) {
            is WithData -> this.data != null
            else -> false
        }
    }
}

inline fun <T, R> ViewResource.WithData<T>.map(crossinline transform: (T) -> R): ViewResource<R> {
    return when (this) {
        is ViewResource.WithData.Success -> ViewResource.WithData.Success(transform(this.data))
        is ViewResource.WithData.Loading -> ViewResource.WithData.Loading(this.data?.let {
            transform(
                it
            )
        })
    }
}
