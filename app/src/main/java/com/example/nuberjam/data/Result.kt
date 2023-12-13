package com.example.nuberjam.data

sealed class Result<out R> private constructor() {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val errorCode: Int) : Result<Nothing>()
//    class Empty<out T> : Result<T>()
    object Loading : Result<Nothing>()
}