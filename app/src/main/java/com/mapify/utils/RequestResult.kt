package com.mapify.utils

sealed class RequestResult {
    data class Success(val message: String) : RequestResult()
    data class Failure(val message: String) : RequestResult()
    object Loading : RequestResult()
}