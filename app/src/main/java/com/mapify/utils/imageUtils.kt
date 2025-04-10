package com.mapify.utils

import android.content.Context
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult

suspend fun isImageValid(context: Context, url: String): Boolean {
    val loader = ImageLoader(context)
    val request = ImageRequest.Builder(context)
        .data(url)
        .allowHardware(false)
        .build()

    return loader.execute(request) is SuccessResult
}